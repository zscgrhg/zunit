package moc.etz.zunit.util;

import shade.zunit.ch.qos.logback.classic.Logger;
import shade.zunit.ch.qos.logback.classic.LoggerContext;
import shade.zunit.ch.qos.logback.classic.joran.JoranConfigurator;
import shade.zunit.ch.qos.logback.core.joran.spi.JoranException;

public class LoggerUtil {
    public static final LoggerContext LOGGER_CONTEXT = createFromClasspathResource();

    public static final Logger of(Class<?> clazz) {
        return LOGGER_CONTEXT.getLogger(clazz);
    }

    public static LoggerContext createFromClasspathResource() {
        LoggerContext context = new LoggerContext();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            String fileName = "sputnik.xml";
            configurator.setContext(context);
            configurator.doConfigure(LoggerUtil.class.getClassLoader().getResourceAsStream(fileName));
        } catch (JoranException e) {
            e.printStackTrace();
        }
        return context;
    }
}
