package moc.etz.zunit.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.parse.RefsInfo;
import moc.etz.zunit.trace.Invocation;
import moc.etz.zunit.trace.TraceReader;
import moc.etz.zunit.trace.TraceReaderImpl;
import moc.etz.zunit.util.LoggerUtil;
import moc.etz.zunit.util.MustacheUtil;
import shade.zunit.ch.qos.logback.classic.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SpecFactory {
    private static final Logger LOGGER = LoggerUtil.of(SpecFactory.class);
    public static final AtomicLong BUILD_INCR = new AtomicLong(1);
    public static final String FN = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    private static TraceReader reader = new TraceReaderImpl();

    @SneakyThrows
    public static SpecModel build(Long subjectInvocationId) {
        Invocation subjectInvocation = reader.readInvocation(subjectInvocationId);
        Class clazz = subjectInvocation.getClazzSource();
        String method = subjectInvocation.getMethod();
        SpecModel specModel = new SpecModel();
        specModel.pkg = clazz.getPackage().getName();
        specModel.subject = clazz.getSimpleName();
        specModel.id = subjectInvocation.id;
        specModel.method = method;
        specModel.className = method + "N" + BUILD_INCR.getAndIncrement() + "Spec";
        specModel.fileName = specModel.subject + FN;
        specModel.subjectDecl = MustacheUtil.format("def subject = new {{0}}()", clazz.getName());

        List<Invocation> children = subjectInvocation.children;

        Map<String, List<GroovyLine>> inputs = new HashMap<>();
        Map<String, List<GroovyLine>> outputs = new HashMap<>();
        Map<String, List<GroovyLine>> returned = new HashMap<>();

        inputs.put(String.valueOf(subjectInvocation.id), buildArgsLine(reader.readInParam(subjectInvocation.id)));
        JsonNode outParam = reader.readOutParam(subjectInvocation.id);
        outputs.put(String.valueOf(subjectInvocation.id), buildArgsLine(outParam));
        returned.put(String.valueOf(subjectInvocation.id), buildRetLine(outParam));

        if (!children.isEmpty()) {

            Map<RefsInfo, List<Invocation>> mapInv = new HashMap<>();
            for (Invocation child : children) {
                inputs.put(String.valueOf(child.id), buildArgsLine(reader.readInParam(child.id)));
                JsonNode childOutParam = reader.readOutParam(child.id);
                outputs.put(String.valueOf(child.id), buildArgsLine(childOutParam));
                returned.put(String.valueOf(child.id), buildRetLine(childOutParam));
                RefsInfo refPath = child.getRefsInfo();
                mapInv.putIfAbsent(refPath, new ArrayList<>());
                mapInv.get(refPath).add(child);
            }
            specModel.mockArgs = mapInv.keySet().stream().anyMatch(ref -> RefsInfo.RefType.ARG.equals(ref.type));
            List<String> mockBlock = mapInv.entrySet().stream().flatMap(e -> buildMockBlock(e).stream()).collect(Collectors.toList());

            specModel.mockBlock = mockBlock;

        }
        specModel.Inputs = inputs.entrySet();
        specModel.Outputs = outputs.entrySet();
        specModel.Returned = returned.entrySet();
        specModel.actionDecl = buildWhen(subjectInvocation);
        specModel.assertDecl = buildAssert(subjectInvocation);
        return specModel;
    }

    public static String buildWhen(Invocation invocation) {
        Class[] argsType = invocation.argsType;
        List<String> argString = new ArrayList<>();
        for (int i = 0; i < argsType.length; i++) {
            String sp = (i < argsType.length - 1) ? "," : "";
            if (RefsInfo.class.isAssignableFrom(argsType[i])) {
                RefsInfo refsInfo = invocation.argsNames.get(i);
                if (RefsInfo.RefType.FIELD.equals(refsInfo.type)) {
                    argString.add(MustacheUtil.format("subject.{{0}}{{1}}", refsInfo.name, sp));
                } else {
                    argString.add(MustacheUtil.format("argMockDefs.{{0}}{{1}}", refsInfo.name, sp));
                }

            } else {
                argString.add(MustacheUtil.format("INPUTS{{0}}[{{1}}]{{2}}", invocation.id, i, sp));
            }
        }

        String action = MustacheUtil.format("def ret=subject.{{0}}({{#1}}{{.}}{{/1}})", invocation.method, argString);
        return action;
    }

    public static String buildAssert(Invocation invocation) {


        return MustacheUtil.format("ret == RETURNED{{0}}", invocation.id);
    }

    public static List<String> buildMockBlock(Map.Entry<RefsInfo, List<Invocation>> invs) {
        List<String> ret = new ArrayList<>();
        List<Invocation> value = invs.getValue();
        Class clazz = value.get(0).getDeclaredClass();

        RefsInfo refsInfo = invs.getKey();

        if (RefsInfo.RefType.FIELD.equals(refsInfo.type)) {
            ret.add(MustacheUtil.format("subject.{{0}}=Mock({{1}}){", refsInfo.name, clazz.getName()));
        } else {
            ret.add(MustacheUtil.format("argMockDefs.{{0}}=Mock({{1}}){", refsInfo.name, clazz.getName()));
        }

        for (Invocation invocation : value) {
            String args = invocation.getSignature().replaceAll("^.*\\((.*?)\\)", "$1");
            String[] argsSplit = args.split(",");
            int length = argsSplit.length;
            //String argsLine = IntStream.range(0, length).mapToObj(i -> "{p" + i + "-> p" + i + "==INPUTS{{1}}[" + i + "]}").collect(Collectors.joining(","));
            String argsLine = IntStream.range(0, length).mapToObj(i -> "INPUTS{{1}}[" + i + "]").collect(Collectors.joining(","));
            String newArgsLine = IntStream.range(0, length).mapToObj(i -> argsSplit[i] + " arg" + i + "").collect(Collectors.joining(","));
            List<String> copyLine = IntStream.range(0, length)
                    .boxed()
                    .flatMap(i -> {
                        String left = "arg" + i + "";
                        String right = MustacheUtil.format("OUTPUTS{{0}}[" + i + "]", invocation.id);
                        String copy = MustacheUtil.format("{{1}}.copyDirtyPropsTo({{0}})", left, right);//
                        return Stream.of(copy);
                    }).collect(Collectors.toList());
            //ret.add(MustacheUtil.format("1 * {{0}}(" + argsLine + ") >> RETURNED{{1}} ", invocation.method, invocation.id));
            ret.add(MustacheUtil.format("1 * {{0}}(" + argsLine + ") >> { " + newArgsLine + "->", invocation.method, invocation.id));
            ret.addAll(copyLine);
            ret.add(MustacheUtil.format("return RETURNED{{0}} ", invocation.id));
            ret.add("}");
        }
        ret.add("}");
        return ret;
    }

    public static List<GroovyLine> buildRetLine(JsonNode paramModel) {
        List<GroovyLine> ret = new ArrayList<>();
        JsonNode returned = paramModel.get("returned");
        JsonNode rgt = paramModel.get("returnedGenericType");
        ret.addAll(jsonToGroovyMap(1, null, returned, rgt.asText(), null));
        endBlock(ret, null);
        return ret;
    }

    public static List<GroovyLine> buildArgsLine(JsonNode paramModel) {
        List<GroovyLine> ret = new ArrayList<>();
        JsonNode args = paramModel.get("args");
        ArrayNode argValues = (ArrayNode) args;
        JsonNode at = paramModel.get("argsType");
        ArrayNode atArr = (ArrayNode) at;

        JsonNode agt = paramModel.get("argsGenericType");
        ArrayNode agtArr = (ArrayNode) agt;
        if (argValues != null && argValues.size() > 0) {
            for (int i = 0; i < argValues.size(); i++) {
                ret.addAll(jsonToGroovyMap(1, null, argValues.get(i), agtArr.get(i).asText(), atArr.get(i).asText()));
            }
        }
        endBlock(ret, null);
        return ret;
    }

    private static List<GroovyLine> jsonToGroovyMap(int ident, String name, JsonNode value) {
        List<GroovyLine> lines = jsonToGroovyMap(ident, name, value, null, null);
        return lines;
    }

    private static void endBlock(List<GroovyLine> lines, String endChar) {
        if (lines != null && !lines.isEmpty()) {
            GroovyLine groovyLine = lines.get(lines.size() - 1);
            groovyLine.setLineEnd(endChar);
        }
    }

    @SneakyThrows
    public static List<GroovyLine> jsonToGroovyMap(int ident, String name, JsonNode value, String genericSignature, String valueType) {
        ident++;
        String identStr = IntStream.range(0, ident).mapToObj(i -> "\t").collect(Collectors.joining());
        List<GroovyLine> defs = new ArrayList<>();

        if (value.isArray()) {
            defs.add(new GroovyLine(identStr, MustacheUtil.format("{{#0}}{{0}}:{{/0}}[", name), null));
            ArrayNode arrayNode = (ArrayNode) value;
            List<GroovyLine> subLines = new ArrayList<>();
            for (int i = 0; i < arrayNode.size(); i++) {
                subLines.addAll(jsonToGroovyMap(ident, null, arrayNode.get(i)));
            }
            endBlock(subLines, null);
            defs.addAll(subLines);
            defs.add(new GroovyLine(identStr, "]"));
        } else if (value == null) {
            defs.add(new GroovyLine(identStr, null));
        } else if (value.isTextual()) {
            defs.add(new GroovyLine(identStr, MustacheUtil.format("{{#0}}{{0}}:{{/0}}'''{{1}}'''", name, value.asText())));
        } else if (value.isValueNode()) {
            defs.add(new GroovyLine(identStr, MustacheUtil.format("{{#0}}{{0}}:{{/0}}{{1}}", name, value.asText())));
        } else {
            assert value.isObject();
            defs.add(new GroovyLine(identStr, MustacheUtil.format("{{#0}}{{0}}:{{/0}}[", name), null));
            Iterator<String> names = value.fieldNames();
            List<GroovyLine> subLines = new ArrayList<>();
            while (names.hasNext()) {
                String nextName = names.next();
                JsonNode subNode = value.get(nextName);
                subLines.addAll(jsonToGroovyMap(ident, nextName, subNode));
            }
            endBlock(subLines, null);
            defs.addAll(subLines);
            defs.add(new GroovyLine(identStr, "]"));
        }
        if (defs.size() > 1 && genericSignature != null && !genericSignature.isEmpty()) {
            GroovyLine groovyLine = defs.get(defs.size() - 1);
            if (RefsInfo.class.getName().equals(valueType)) {
                groovyLine.tokens = MustacheUtil.format("{{0}} as {{1}}", groovyLine.tokens, RefsInfo.class.getName());
            } else if (genericSignature.contains("<")) {
                groovyLine.tokens = MustacheUtil.format("{{0}}.reconstruction(new TypeReference<{{1}}>(){})", groovyLine.tokens, genericSignature);
            } else {
                groovyLine.tokens = MustacheUtil.format("{{0}} as {{1}}", groovyLine.tokens, genericSignature);
            }

        }

        return defs;
    }

    @SneakyThrows
    public static void writeSpec(Long subjectInvocationId) {
        SpecModel model = build(subjectInvocationId);
        Path pkg = TraceConfig.INSTANCE
                .getSpecOutputsDir()
                .toPath()
                .resolve(model.getPkg());
        File pkgDir = pkg.toFile();
        if (!pkgDir.exists()) {
            pkgDir.mkdirs();
        }

        model.imports = true;
        model.className = model.fileName + model.className;
        String specText = MustacheUtil.render("btm/spec.mustache", model);
        Path resolve = pkg.resolve(model.className + ".groovy");
        LOGGER.debug("write :" + resolve.toString());
        Files.write(resolve,
                specText.getBytes("UTF-8"), StandardOpenOption.CREATE);
    }
}
