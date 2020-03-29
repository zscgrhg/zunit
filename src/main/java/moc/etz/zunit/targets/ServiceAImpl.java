package moc.etz.zunit.targets;

import moc.etz.zunit.parse.annotation.Trace;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceAImpl implements ServiceA {
    @Trace
    ServiceB serviceB = new ServiceBImpl();

    @Override
    public List<HelloEntity<String>> hello(int x, List<String> arg2) {
        List<String> args = serviceB.doServiceB(x, x + 1, x + 2);
        List<HelloEntity<String>> ret = args.stream().map(s -> {
            HelloEntity<String> helloEntity = new HelloEntity();
            helloEntity.data = s;
            helloEntity.counter = x;
            helloEntity.name = "ok";
            return helloEntity;
        }).collect(Collectors.toList());
        return ret;
    }


    @Override
    public Object[] arrayTest(String[] p) {
        return new Object[0];
    }
}
