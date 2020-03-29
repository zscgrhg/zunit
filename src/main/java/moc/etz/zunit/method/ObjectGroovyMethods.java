package moc.etz.zunit.method;

import com.fasterxml.jackson.core.type.TypeReference;
import moc.etz.zunit.util.JsonUtil;

public class ObjectGroovyMethods {
    public static <T> T reconstruction(Object target, TypeReference<T> genericSignature) {
        String json = JsonUtil.write(target);
        return JsonUtil.readerFor(genericSignature, json);
    }
}
