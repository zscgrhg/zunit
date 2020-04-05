package moc.etz.zunit.trace.proxy;

import lombok.SneakyThrows;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;

public class SpringProxyResolverImpl implements ProxyResolver {
    @Override
    public Class getTargetClass(Object proxy) {
        if (proxy instanceof TargetClassAware) {
            return ((TargetClassAware) proxy).getTargetClass();
        }
        return null;
    }

    @Override
    @SneakyThrows
    public Object getTargetSource(Object proxy) {
        if (proxy instanceof TargetSource) {
            return ((TargetSource) proxy).getTarget();
        }
        return null;
    }

    @Override
    public boolean isProxy(Object proxy) {
        return AopUtils.isAopProxy(proxy);
    }

    @Override
    public Class findOwner(Object proxy, Method m) {
        return getTargetClass(proxy);
    }
}
