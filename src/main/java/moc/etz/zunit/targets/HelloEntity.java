package moc.etz.zunit.targets;

import lombok.Data;

@Data
public class HelloEntity<T> {
    String name;
    int counter;
    T data;
}
