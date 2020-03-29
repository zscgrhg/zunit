package moc.etz.zunit.trace;

public interface ParamWriter {
    void write(ParamModel paramModel);

    void write(InvocationContext context);
}
