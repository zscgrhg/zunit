package moc.etz.zunit.parse;

import moc.etz.zunit.parse.annotation.TestSubject;
import moc.etz.zunit.util.ClassUtil;

public class DefaultTestSubjectSelectorImpl implements TestSubjectSelector {
    @Override
    public boolean selectTestSubject(Class clazz) {
        return ClassUtil.hasAnnotation(clazz, TestSubject.class);
    }


}
