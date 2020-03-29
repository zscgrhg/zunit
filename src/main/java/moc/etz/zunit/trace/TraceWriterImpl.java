package moc.etz.zunit.trace;

import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class TraceWriterImpl implements TraceWriter {


    @Override
    @SneakyThrows
    public void write(ParamModel paramModel) {
        Path file = TraceConfig.INSTANCE.getTraceOutputsDir().toPath().resolve(paramModel.invocationId + "." + paramModel.name + ".json");
        Files.copy(new ByteArrayInputStream(JsonUtil.write(paramModel).getBytes("UTF8")),
                file,
                StandardCopyOption.REPLACE_EXISTING);
    }

    @SneakyThrows
    @Override
    public void write(InvocationContext context) {

        List<Invocation> nodes = context.getNodes();
        for (Invocation node : nodes) {
            Path file = TraceConfig.INSTANCE.getTraceOutputsDir().toPath().resolve(node.id + ".subject.json");
            Files.copy(new ByteArrayInputStream(JsonUtil.write(node).getBytes("UTF8")),
                    file,
                    StandardCopyOption.REPLACE_EXISTING);
        }

    }

}
