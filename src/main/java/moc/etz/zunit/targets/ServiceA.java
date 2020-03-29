package moc.etz.zunit.targets;

import java.util.List;
import java.util.Set;

public interface ServiceA {
    List<Set<String>> hello(List<Set<HelloEntity<String>>> p);
}
