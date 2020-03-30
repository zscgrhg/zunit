package moc.etz.zunit.instrument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class MethodNames {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodNames.class);
    public static final AtomicLong NAMES_INCR = new AtomicLong(1);
    public static final Map<Long, MethodNames> METHOD_NAMES_MAP = new ConcurrentHashMap<>();
    public static final String BIND_NAME = "_moc_etz_zunit_instrument_MethodNames_mid_";
    @JsonIgnore
    public final Long mid = NAMES_INCR.getAndIncrement();
    public final String ownerName;
    public final Class owner;
    public final String ownerType;
    public final String name;
    public final String genericSymbol;
    public final String symbol;
    public final String genericSignature;
    public final String signature;
    public final String genericReturned;
    public final String returned;
    public final String[] genericArgs;

    private MethodNames(Method m) {
        this.owner = m.getDeclaringClass();
        this.ownerType = owner.isInterface() ? "INTERFACE" : "CLASS";
        this.ownerName = owner.getName();
        this.name = m.getName();
        this.genericSymbol = normalizeVarargs(m.toGenericString());
        this.symbol = removeGeneric(genericSymbol);
        int argStart = genericSymbol.indexOf('(');
        int argEnd = genericSymbol.indexOf(')');
        String noArgString = genericSymbol.substring(0, argStart);
        int idx = noArgString.lastIndexOf('.') + 1;
        this.genericSignature = genericSymbol.substring(idx);
        this.signature = removeGeneric(genericSignature);
        this.genericReturned = m.getGenericReturnType().getTypeName();
        this.returned = removeGeneric(genericReturned);
        this.genericArgs = genericSymbol.substring(argStart + 1, argEnd).split(",");

    }

    public static MethodNames build(Method m) {
        MethodNames methodNames = new MethodNames(m);
        METHOD_NAMES_MAP.putIfAbsent(methodNames.mid, methodNames);
        LOGGER.debug(methodNames.mid + " is mapping to :" + methodNames.genericSymbol);
        return methodNames;
    }

    public static String removeGeneric(String name) {
        if (name == null) {
            return name;
        }
        String rp = name.replaceAll("<[^<>]*>", "");
        if (rp.contains("<")) {
            return removeGeneric(rp);
        }
        return rp;
    }

    public static String normalizeVarargs(String genericString) {
        return genericString.replaceAll("\\Q...\\E", "[]");
    }
}
