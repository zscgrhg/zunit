package moc.etz.zunit.targets;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ServiceBImpl implements ServiceB {
    @Override
    public List<String> doServiceB(int... args) {
        return IntStream.of(args).mapToObj(i -> String.valueOf(i)).collect(Collectors.toList());
    }
}
