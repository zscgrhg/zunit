package moc.etz.zunit.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import moc.etz.zunit.config.TraceConfig;
import moc.etz.zunit.trace.Invocation;
import moc.etz.zunit.trace.TraceReader;
import moc.etz.zunit.trace.TraceReaderImpl;
import moc.etz.zunit.util.MustacheUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpecFactory {
    public static final AtomicLong BUILD_INCR = new AtomicLong(1);
    public static final String FN = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    private static TraceReader reader = new TraceReaderImpl();

    @SneakyThrows
    public static SpecModel build(Long subjectInvocationId) {
        Invocation subjectInvocation = reader.readInvocation(subjectInvocationId);
        Class clazz = subjectInvocation.getClazz();
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

            Map<String, List<Invocation>> mapInv = new HashMap<>();
            for (Invocation child : children) {
                inputs.put(String.valueOf(child.id), buildArgsLine(reader.readInParam(child.id)));
                JsonNode childOutParam = reader.readOutParam(child.id);
                outputs.put(String.valueOf(child.id), buildArgsLine(childOutParam));
                returned.put(String.valueOf(child.id), buildRetLine(childOutParam));
                String refPath = child.getRefPath();
                mapInv.putIfAbsent(refPath, new ArrayList<>());
                mapInv.get(refPath).add(child);
            }

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
        String action = MustacheUtil.format("def ret=subject.{{0}}(*INPUTS{{1}})", invocation.method, invocation.id);
        return action;
    }

    public static String buildAssert(Invocation invocation) {


        return MustacheUtil.format("ret == RETURNED{{0}}", invocation.id);
    }

    public static List<String> buildMockBlock(Map.Entry<String, List<Invocation>> invs) {
        List<String> ret = new ArrayList<>();
        List<Invocation> value = invs.getValue();
        Class clazz = value.get(0).getDeclaredClass();
        ret.add(MustacheUtil.format("subject.{{0}}=Mock({{1}}){", invs.getKey(), clazz.getName()));
        for (Invocation invocation : value) {
            String args = invocation.getSignature().replaceAll("^.*\\((.*?)\\)", "$1");
            int length = args.split(",").length;
            //String argsLine = IntStream.range(0, length).mapToObj(i -> "{p" + i + "-> p" + i + "==INPUTS{{1}}[" + i + "]}").collect(Collectors.joining(","));
            String argsLine = IntStream.range(0, length).mapToObj(i -> "INPUTS{{1}}[" + i + "]").collect(Collectors.joining(","));
            String newArgsLine = IntStream.range(0, length).mapToObj(i -> "arg" + i + "").collect(Collectors.joining(","));
            List<String> copyLine = IntStream.range(0, length)
                    .mapToObj(i -> {
                        String left = "arg" + i + "";
                        String right = MustacheUtil.format("OUTPUTS{{0}}[" + i + "]", invocation.id);
                        String copy = MustacheUtil.format("if(null!={{1}}&&{{0}}!={{1}}){InvokerHelper.setProperties({{0}}, {{1}}.properties)}", left, right);
                        return copy;
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
        ret.addAll(jsonToGroovyMap(1, null, returned, rgt.asText()));
        endBlock(ret, null);
        return ret;
    }

    public static List<GroovyLine> buildArgsLine(JsonNode paramModel) {
        List<GroovyLine> ret = new ArrayList<>();
        JsonNode args = paramModel.get("args");
        ArrayNode argValues = (ArrayNode) args;
        JsonNode agt = paramModel.get("argsGenericType");
        ArrayNode agtArr = (ArrayNode) agt;
        if (argValues != null && argValues.size() > 0) {
            for (int i = 0; i < argValues.size(); i++) {
                ret.addAll(jsonToGroovyMap(1, null, argValues.get(i), agtArr.get(i).asText()));
            }
        }
        endBlock(ret, null);
        return ret;
    }

    private static List<GroovyLine> jsonToGroovyMap(int ident, String name, JsonNode value) {
        List<GroovyLine> lines = jsonToGroovyMap(ident, name, value, null);
        return lines;
    }

    private static void endBlock(List<GroovyLine> lines, String endChar) {
        if (lines != null) {
            GroovyLine groovyLine = lines.get(lines.size() - 1);
            groovyLine.setLineEnd(endChar);
        }
    }

    @SneakyThrows
    public static List<GroovyLine> jsonToGroovyMap(int ident, String name, JsonNode value, String genericSignature) {
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
            defs.add(new GroovyLine(identStr, MustacheUtil.format("{{#0}}{{0}}:{{/0}}'{{1}}'", name, value.asText())));
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
            if (genericSignature.contains("<")) {
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
        Files.write(pkg.resolve(model.className + ".groovy"),
                specText.getBytes("UTF-8"), StandardOpenOption.CREATE);
    }
}
