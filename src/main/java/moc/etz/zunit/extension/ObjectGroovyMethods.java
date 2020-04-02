package moc.etz.zunit.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import groovy.lang.Closure;
import moc.etz.zunit.util.JsonUtil;

public class ObjectGroovyMethods {
    public static <T> T reconstruction(Object target, TypeReference<T> genericSignature) {
        String json = JsonUtil.write(target);
        return JsonUtil.readerFor(genericSignature, json);
    }

    public static <V> V reconstruction(Object target, Closure<V> closure) {
        return closure.call(target);
    }
}
