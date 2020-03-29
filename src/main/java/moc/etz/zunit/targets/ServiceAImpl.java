package moc.etz.zunit.targets;

import moc.etz.zunit.parse.annotation.Trace;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ServiceAImpl implements ServiceA {


    @Trace
    ServiceB serviceB = new ServiceBImpl();

    @Override
    public List<HelloEntity<String>> hello(int x, List<String> arg2) {
        List<HelloEntity<String>> ret = IntStream.range(1, x).parallel().mapToObj(i -> Integer.valueOf(i)).flatMap(xx -> {
            List<String> args = serviceB.doServiceB(xx);
            Stream<HelloEntity<String>> objectStream = args.stream().map(s -> {
                HelloEntity<String> helloEntity = new HelloEntity();
                helloEntity.data = s;
                helloEntity.counter = x;
                helloEntity.name = "ok";
                return helloEntity;
            });
            return objectStream;
        }).collect(Collectors.toList());

        return ret;
    }


    @Override
    public Object[] arrayTest(String[] p) {
        return new Object[0];
    }
}
