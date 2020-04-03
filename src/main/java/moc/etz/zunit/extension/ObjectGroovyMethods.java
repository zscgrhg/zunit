package moc.etz.zunit.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import groovy.lang.Closure;
import lombok.SneakyThrows;
import moc.etz.zunit.util.JsonUtil;
import net.sf.cglib.beans.BeanCopier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ObjectGroovyMethods {
    public static <T> T reconstruction(Object target, TypeReference<T> genericSignature) {
        String json = JsonUtil.write(target);
        return JsonUtil.readerFor(genericSignature, json);
    }

    public static <V> V reconstruction(Object target, Closure<V> closure) {
        return closure.call(target);
    }

    @SneakyThrows
    public static <V> V copyIfDirty(V source, V target) {
        Class<?> sclazz = source.getClass();
        if (sclazz.isArray()) {
            Object[] s = (Object[]) source;
            Object[] t = (Object[]) target;
            for (int i = 0; i < Math.min(s.length, t.length); i++) {
                copyIfDirty(s[i], t[i]);
            }
        } else if (Collection.class.isAssignableFrom(sclazz)) {
            List s = new ArrayList();
            s.addAll((Collection) source);
            List t = new ArrayList();
            t.addAll((Collection) target);
            for (int i = 0; i < Math.min(s.size(), t.size()); i++) {
                copyIfDirty(s.get(i), t.get(i));
            }
        } else {
            if (Objects.equals(source, target)) {
                return target;
            }
            BeanCopier beanCopier = BeanCopier.create(target.getClass(), source.getClass(), false);
            beanCopier.copy(source, target, null);
        }
        return target;
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
