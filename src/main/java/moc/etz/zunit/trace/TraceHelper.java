package moc.etz.zunit.trace;

import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.instrument.MethodNames;
import moc.etz.zunit.trace.proxy.ProxyResolver;
import moc.etz.zunit.util.LoggerUtil;
import org.jboss.byteman.rule.Rule;
import shade.zunit.ch.qos.logback.classic.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import static moc.etz.zunit.util.ClassUtil.resolve;

public class TraceHelper {
    private static final Logger LOGGER = LoggerUtil.of(TraceHelper.class);
    private static final ProxyResolver RESOLVER = TraceConfig.INSTANCE.getProxyResolver();
    Rule rule;


    public TraceHelper(Rule rule) {
        this.rule = rule;
    }

    @SneakyThrows
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
            invocation.setThisObjectSource(thisObject);
            invocation.staticInvoke = thisObject == null;
            invocation.setClazzThis(thisObject == null ? names.context : thisObject.getClass());
            Class c = invocation.clazzThis;
            if (thisObject != null && RESOLVER.isProxy(thisObject)) {
                Object targetSource = RESOLVER.getTargetSource(thisObject);
                Class targetClass = RESOLVER.getTargetClass(thisObject);
                if (targetSource != null) {
                    invocation.thisObjectSource = targetSource;
                    c = targetSource.getClass();
                } else if (targetClass != null) {
                    c = targetClass;
                } else {
                    c = RESOLVER.findOwner(thisObject, names.method);
                }
            }
            invocation.clazzSource = c;
            invocation.subject = MethodNames.isTestSubject(invocation.clazzSource, invocation.mid);
            invocation.saveObjectsRef(names.genericSymbol, methodArgs);
            //Method method = c.getMethod(names.name, names.parametersType);
            invocation.genericReturned = resolve(names.method.getGenericReturnType(), c).getTypeName();
            invocation.genericArgs = getGenericArgs(names.method, c);
            context.push(invocation, methodArgs);
        }
    }

    public void atExit(Long mid, Object[] args) {
        LOGGER.debug(mid + ",trigger by " + rule.getName());
        InvocationContext context = InvocationContext.getCurrent(false);
        if (context != null && context.canPop()) {
            Object[] methodArgs = Stream.of(args).skip(1).toArray();
            context.pop(methodArgs, null, null);
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


    public String[] getGenericArgs(Method m, Class inheritorClass) {
        Type[] parameterTypes = m.getGenericParameterTypes();
        return Stream.of(parameterTypes).map(t -> resolve(t, inheritorClass).getTypeName()).toArray(String[]::new);
    }


}
