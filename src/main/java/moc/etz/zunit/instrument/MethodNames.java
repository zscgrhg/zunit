package moc.etz.zunit.instrument;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class MethodNames {
    public final String ownerName;
    public final String ownerType;
    public final String genericSymbol;
    public final String symbol;
    public final String genericSignature;
    public final String signature;
    public final String genericReturned;
    public final String returned;

    public MethodNames(Method m) {
        Class owner = m.getDeclaringClass();
        this.ownerType = owner.isInterface() ? "INTERFACE" : "CLASS";
        this.ownerName = owner.getName();

        this.genericSymbol = m.toGenericString();
        this.symbol = removeGeneric(genericSymbol);
        String noArgString = genericSymbol.substring(0, genericSymbol.indexOf('('));
        int idx = noArgString.lastIndexOf('.') + 1;
        this.genericSignature = genericSymbol.substring(idx);
        this.signature = removeGeneric(genericSignature);
        this.genericReturned = m.getGenericReturnType().getTypeName();
        this.returned = removeGeneric(genericReturned);
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
}
