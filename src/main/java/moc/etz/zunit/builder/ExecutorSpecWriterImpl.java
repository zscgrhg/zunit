package moc.etz.zunit.builder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorSpecWriterImpl implements SpecWriter {
    private static final ExecutorService SPEC_FACTORY = Executors.newFixedThreadPool(16);

    @Override
    public void write(Long invocationId) {
        SPEC_FACTORY.submit(() -> SpecFactory.writeSpec(invocationId));
    }
}
