package moc.etz.zunit.builder;

import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.trace.Invocation;
import moc.etz.zunit.trace.TraceReader;
import moc.etz.zunit.trace.TraceReaderImpl;
import moc.etz.zunit.util.MustacheUtil;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SpecFactory {

    static TraceReader reader = new TraceReaderImpl();

    @SneakyThrows
    public static SpecModel build(Long subjectInvocationId) {
        Invocation subjectInvocation = reader.readInvocation(subjectInvocationId);
        Class clazz = subjectInvocation.getClazz();
        String method = subjectInvocation.getMethod();
        SpecModel specModel = new SpecModel();
        specModel.subject = clazz.getSimpleName();
        specModel.id = subjectInvocation.id;
        specModel.method = method;
        specModel.fileName = specModel.subject + method + specModel.id + "Spec.groovy";
        specModel.subjectDecl = MustacheUtil.format("def subject = new {{0}}()", clazz.getName());
        return specModel;
    }

    @SneakyThrows
    public static void writeSpec(Long subjectInvocationId) {
        SpecModel model = build(subjectInvocationId);
        String specText = MustacheUtil.render("btm/spec.mustache", model);
        Files.copy(new ByteArrayInputStream(specText.getBytes("UTF-8")),
                TraceConfig.INSTANCE
                        .getSpecOutputsDir()
                        .toPath()
                        .resolve(model.fileName), StandardCopyOption.REPLACE_EXISTING);
    }
}
