package moc.etz.zunit.targets;

import moc.etz.zunit.parse.annotation.TestSubject;

import java.util.List;

@TestSubject
public interface ServiceA {


    List<HelloEntity<String>> hello(int x, List<String> arg2);

    Object[] arrayTest(String[] p);
}
