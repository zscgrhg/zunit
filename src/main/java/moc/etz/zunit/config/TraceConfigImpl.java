package moc.etz.zunit.config;

import lombok.SneakyThrows;
import moc.etz.zunit.ZUnit;
import moc.etz.zunit.builder.ExecutorSpecWriterImpl;
import moc.etz.zunit.builder.SpecWriter;
import moc.etz.zunit.trace.TraceWriterImpl;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;


public class TraceConfigImpl implements TraceConfig {
    public static final File workspace = new File("data").toPath().resolve(ymdHmsOfNow()).toFile();
    public static final File specDir = Paths.get("src\\test\\groovy\\")
            .toFile();

    static {
        if (!workspace.exists()) {
            workspace.mkdirs();
        }

        if (!specDir.exists()) {
            specDir.mkdirs();
        }
    }

    @SneakyThrows
    public static String ymdHmsOfNow() {
        Properties p = new Properties();
        p.load(TraceWriterImpl.class.getClassLoader().getResourceAsStream("git.properties"));
        return p.getProperty("git.commit.id.abbrev") + "/"
                + new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss").format(new Date());
    }

    @Override
    public File getTraceOutputsDir() {
        return workspace;
    }

    @Override
    public File getSpecOutputsDir() {
        return specDir;
    }

    @Override
    public SpecWriter getSpecWriter() {
        SpecWriter specWriter = Optional.ofNullable(ZUnit.CONFIG.getProperty("zunit.spec.writer")).map(s -> {
            try {
                Class<?> swImpl = Class.forName(s);
                return (SpecWriter) swImpl.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(new ExecutorSpecWriterImpl());
        return specWriter;
    }

    @Override
    public boolean groopByClass() {
        boolean groopByClass = Optional.ofNullable(ZUnit.CONFIG.getProperty("zunit.spec.groop-by-class"))
                .map("true"::equalsIgnoreCase).orElse(true);
        return groopByClass;
    }
}
