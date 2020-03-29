package moc.etz.zunit.config;

import java.io.File;

public interface TraceConfig {
    TraceConfig INSTANCE = new TraceConfigImpl();

    File getTraceOutputsDir();

    File getSpecOutputsDir();
}
