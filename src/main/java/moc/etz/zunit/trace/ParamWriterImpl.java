package moc.etz.zunit.trace;

import lombok.SneakyThrows;
import moc.etz.zunit.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ParamWriterImpl implements ParamWriter {

    public static final File workspace = new File("data").toPath().resolve(ymdHmsOfNow()).toFile();

    static {
        if (!workspace.exists()) {
            workspace.mkdirs();
        }
    }

    @SneakyThrows
    public static String ymdHmsOfNow() {
        Properties p = new Properties();
        p.load(ParamWriterImpl.class.getClassLoader().getResourceAsStream("git.properties"));
        return p.getProperty("git.commit.id.abbrev") + "/"
                + new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss").format(new Date());
    }

    @Override
    @SneakyThrows
    public void write(ParamModel paramModel) {
        Path file = workspace.toPath().resolve(paramModel.invocationId + "." + paramModel.name + ".json");
        Files.copy(new ByteArrayInputStream(JsonUtil.write(paramModel).getBytes("UTF8")),
                file,
                StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    @Override
    public void write(InvocationContext context) {

        List<Invocation> nodes = context.getNodes();
        for (Invocation node : nodes) {
            Path file = workspace.toPath().resolve(node.id + ".subject.json");
            Files.copy(new ByteArrayInputStream(JsonUtil.write(node).getBytes("UTF8")),
                    file,
                    StandardCopyOption.REPLACE_EXISTING);
        }

    }

}
