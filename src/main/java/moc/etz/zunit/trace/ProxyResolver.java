package moc.etz.zunit.trace;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

public interface ProxyResolver {


    static Class findFirstOwnerForJdkProxy(Object proxyObject, Method method) {
        Class proxy = proxyObject.getClass();
        Type[] genericInterfaces = proxy.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            assert type instanceof Class;
            Class clazz = (Class) type;
            boolean anyMatch = Stream.of(clazz.getMethods()).anyMatch(method::equals);
            if (anyMatch) {
                return clazz;
            }
        }
        return proxy;
    }

    Class getTargetClass(Object proxy);

    Object getTargetSource(Object proxy);

    boolean isProxy(Object proxy);

    Class findFirstOwner(Object proxy, Method m);
}
