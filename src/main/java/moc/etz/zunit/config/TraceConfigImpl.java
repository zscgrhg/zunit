package moc.etz.zunit.config;

import lombok.SneakyThrows;
import moc.etz.zunit.trace.TraceWriterImpl;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
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
}
