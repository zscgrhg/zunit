package moc.etz.zunit.extension;

import com.fasterxml.jackson.core.type.TypeReference;
import groovy.lang.Closure;
import lombok.SneakyThrows;
import moc.etz.zunit.parse.RefsInfo;
import moc.etz.zunit.util.JsonUtil;
import net.sf.cglib.beans.BeanCopier;
import org.codehaus.groovy.runtime.NullObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
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
    public static <V> void copyDirtyPropsTo(V source, V target) {
        if (Objects.equals(source, target) || source == null || target == null
                || source instanceof NullObject || target instanceof NullObject || source instanceof RefsInfo) {
            return;
        }
        Class<?> targetClass = target.getClass();
        Class<?> sourceClass = source.getClass();
        if (targetClass.isArray()) {
            Object[] s = (Object[]) source;
            Object[] t = (Object[]) target;
            for (int i = 0; i < Math.min(s.length, t.length); i++) {
                t[i] = s[i];
            }
        } else if (Collection.class.isAssignableFrom(targetClass)) {
            Collection c = (Collection) target;
            c.clear();
            c.addAll((Collection) source);
        } else {
            if (Objects.equals(source, target)) {
                return;
            }
            BeanCopier beanCopier = BeanCopier.create(target.getClass(), sourceClass, false);
            beanCopier.copy(source, target, null);
        }
        return;
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
