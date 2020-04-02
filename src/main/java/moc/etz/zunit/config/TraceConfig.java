package moc.etz.zunit.config;

import moc.etz.zunit.builder.SpecWriter;

import java.io.File;

public interface TraceConfig {
    TraceConfig INSTANCE = new TraceConfigImpl();

    File getTraceOutputsDir();

    File getSpecOutputsDir();

    SpecWriter getSpecWriter();
}
