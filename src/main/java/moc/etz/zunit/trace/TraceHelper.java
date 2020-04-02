package moc.etz.zunit.trace;

import moc.etz.zunit.instrument.MethodNames;
import org.jboss.byteman.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class TraceHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceHelper.class);
    Rule rule;

    public TraceHelper(Rule rule) {
        this.rule = rule;
    }


    public void atEntry(Long mid, Object[] args) {
        LOGGER.debug(mid + ",trigger by " + rule.getName());
        InvocationContext context = InvocationContext.getCurrent(true);
        if (context.canPush()) {
            Invocation invocation = new Invocation();
            invocation.mid = mid;
            Object[] methodArgs = Stream.of(args).skip(1).toArray();
            MethodNames names = MethodNames.METHOD_NAMES_MAP.get(mid);
            invocation.setMethod(names.name);
            invocation.setSignature(names.signature);
            Object thisObject = args[0];
            invocation.setThisObject(thisObject);
            invocation.staticInvoke = thisObject == null;
            invocation.setClazz(thisObject == null ? names.owner : thisObject.getClass());
            invocation.saveObjectsRef(names.genericSymbol, methodArgs);
            context.push(invocation, methodArgs);
        }
    }

    public void atExit(Long mid, Object[] args, Object ret) {
        LOGGER.debug(mid + ",trigger by " + rule.getName());
        InvocationContext context = InvocationContext.getCurrent(false);
        if (context != null && context.canPop()) {
            Object[] methodArgs = Stream.of(args).skip(1).toArray();
            context.pop(methodArgs, ret, null);
        }
    }

    public void atException(Long mid, Object[] args, Throwable t) {
        LOGGER.debug(mid + ",trigger by " + rule.getName());
        InvocationContext context = InvocationContext.getCurrent(false);
        if (context != null && context.canPop()) {
            Object[] methodArgs = Stream.of(args).skip(1).toArray();
            context.pop(methodArgs, null, t);
        }
    }
}
