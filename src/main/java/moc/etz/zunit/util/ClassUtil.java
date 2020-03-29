package moc.etz.zunit.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

public class ClassUtil {
    public static boolean hasAnnotation(Class target, Class anno) {
        if (target == null || Object.class.equals(target)) {
            return false;
        }
        Annotation[] annotations = target.getAnnotationsByType(anno);
        return (annotations != null && annotations.length > 0) ||
                hasAnnotation(target.getSuperclass(), anno) ||
                Stream.of(target.getInterfaces()).anyMatch(sf -> hasAnnotation(sf, anno));

    }

    public static boolean hasAnnotation(Field target, Class anno) {
        if (target == null) {
            return false;
        }

        Annotation[] annotations = target.getAnnotationsByType(anno);
        return (annotations != null && annotations.length > 0);

    }

    public static boolean hasAnnotation(Parameter target, Class anno) {
        if (target == null) {
            return false;
        }

        Annotation[] annotations = target.getAnnotationsByType(anno);
        return (annotations != null && annotations.length > 0);

    }
}
