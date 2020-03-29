package moc.etz.zunit.trace;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.util.JsonUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class TraceReaderImpl implements TraceReader {
    private File getWorkDir() {
        return TraceConfig.INSTANCE.getTraceOutputsDir();
    }

    @SneakyThrows
    private JsonNode readFile(String fileName) {
        Path input = getWorkDir().toPath().resolve(fileName);
        byte[] bytes = Files.readAllBytes(input);
        JsonNode jsonNode = JsonUtil.readTree(bytes);
        return jsonNode;
    }

    @SneakyThrows
    private <T> T readFile(Class<T> clazz, String fileName) {
        byte[] bytes = Files.readAllBytes(getWorkDir().toPath().resolve(fileName));
        T model = JsonUtil.readerFor(clazz, bytes);
        return model;
    }

    @Override
    @SneakyThrows
    public JsonNode readInParam(Long invocationId) {
        return readFile(invocationId + ".in.json");
    }

    @SneakyThrows
    @Override
    public JsonNode readOutParam(Long invocationId) {
        return readFile(invocationId + ".out.json");
    }


    @Override
    public Invocation readInvocation(Long invocationId) {
        return readFile(Invocation.class, invocationId + ".subject.json");
    }
}
