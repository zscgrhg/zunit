package moc.etz.zunit.config;

import moc.etz.zunit.builder.SpecWriter;
import moc.etz.zunit.trace.proxy.ProxyResolver;

import java.io.File;

public interface TraceConfig {
    TraceConfig INSTANCE = new TraceConfigImpl();

    File getTraceOutputsDir();

    File getSpecOutputsDir();

    SpecWriter getSpecWriter();

    ProxyResolver getProxyResolver();

    boolean groopByClass();
}
