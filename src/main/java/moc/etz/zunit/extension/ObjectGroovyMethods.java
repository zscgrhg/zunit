package moc.etz.zunit.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import groovy.lang.Closure;
import lombok.SneakyThrows;
import moc.etz.zunit.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectGroovyMethods {
    public static <T> T reconstruction(Object target, TypeReference<T> genericSignature) {
        String json = JsonUtil.write(target);
        return JsonUtil.readerFor(genericSignature, json);
    }

    public static <V> V reconstruction(Object target, Closure<V> closure) {
        return closure.call(target);
    }

    @SneakyThrows
    public static <V> V deepCopy(V target) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(target);
        oos.flush();
        ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);
        return (V) ois.readObject();
    }
}
