package moc.etz.zunit.instrument;

import moc.etz.zunit.trace.TraceHelper;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TraceUtil {
    private static final Map<Class, Boolean> TRACED = new ConcurrentHashMap<>();

    public static void traceInterfaces(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        for (Class anInterface : interfaces) {
            if (!anInterface.getPackage().getName().startsWith("java")) {
                traceInvocation(anInterface);
            }
        }
        traceInvocation(clazz);
    }

    public static boolean shouldIgnore(Method method) {
        return method.isSynthetic() || Modifier.isStatic(method.getModifiers())
                || Modifier.isPrivate(method.getModifiers());
    }

    public static void traceInvocation(Class clazz) {
        Boolean exist = TRACED.putIfAbsent(clazz, true);
        if (exist != null) {
            return;
        }
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (shouldIgnore(method)) {
                continue;
            }
            MethodNames names = MethodNames.build(method);
            {

                BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
                model.setHelper(TraceHelper.class);
                model.addAction(MustacheUtil.format("atEntry({{0}},$*);", MethodNames.BIND_NAME));
                String rule = MustacheUtil.render(model);
                ScriptText scriptText = new ScriptText(model.ruleId, rule);
                scriptTexts.add(scriptText);
            }
            {
                BMRuleMustacheModel model = BMRuleMustacheModel.atExit(names, true);
                model.setHelper(TraceHelper.class);
                model.addAction(MustacheUtil.format("atExit({{0}},$*,$!);", MethodNames.BIND_NAME));
                String rule = MustacheUtil.render(model);
                ScriptText scriptText = new ScriptText(model.ruleId, rule);
                scriptTexts.add(scriptText);
            }
            {
                BMRuleMustacheModel model = BMRuleMustacheModel.atException(names, true);
                model.setHelper(TraceHelper.class);
                model.addAction(MustacheUtil.format("atException({{0}},$*,$^);", MethodNames.BIND_NAME));
                String rule = MustacheUtil.render(model);
                ScriptText scriptText = new ScriptText(model.ruleId, rule);
                scriptTexts.add(scriptText);
            }
        }
        BMUtil.submitText(scriptTexts);
    }
}
