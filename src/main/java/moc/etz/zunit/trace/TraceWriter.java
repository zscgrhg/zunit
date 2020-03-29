package moc.etz.zunit.trace;

public interface TraceWriter {
    void write(ParamModel paramModel);

    void write(InvocationContext context);
}
