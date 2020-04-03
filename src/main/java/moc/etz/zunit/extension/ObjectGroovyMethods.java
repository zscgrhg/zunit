package moc.etz.zunit.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import groovy.lang.Closure;
import lombok.SneakyThrows;
import moc.etz.zunit.util.JsonUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectGroovyMethods {
    public static <T> T reconstruction(Object target, TypeReference<T> genericSignature) {
        String json = JsonUtil.write(target);
        return JsonUtil.readerFor(genericSignature, json);
    }

    public static <V> V reconstruction(Object target, Closure<V> closure) {
        return closure.call(target);
    }

    @SneakyThrows
    public static <V> V copyTo(V source, V target) {
        Class<?> sclazz = source.getClass();
        if (sclazz.isArray()) {
            Object[] s = (Object[]) source;
            Object[] t = (Object[]) target;
            for (int i = 0; i < Math.min(s.length, t.length); i++) {
                copyTo(s[i], t[i]);
            }
        } else if (Collection.class.isAssignableFrom(sclazz)) {
            List s = new ArrayList();
            s.addAll((Collection) source);
            List t = new ArrayList();
            t.addAll((Collection) target);
            for (int i = 0; i < Math.min(s.size(), t.size()); i++) {
                copyTo(s.get(i), t.get(i));
            }
        } else {
            // BeanUtils.copyProperties(u1, u);
            BeanUtils.copyProperties(target, source);
        }

        return source;
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
