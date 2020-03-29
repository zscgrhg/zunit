package moc.etz.zunit.instrument;

import lombok.SneakyThrows;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class TtlWrapperUtil {
    @SneakyThrows
    public static void wrapperExecutor() {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method execute = Executor.class.getDeclaredMethod("execute", Runnable.class);
        MethodNames names = MethodNames.build(execute);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("$0.execute(com.alibaba.ttl.TtlRunnable.get($1));");
            model.addAction("return;");
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.ruleId, rule);
            scriptTexts.add(scriptText);
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atExit(names, true);
            model.setHelper(TtlHelper.class);
            model.addAction(MustacheUtil.format("cleanBarrier({{0}});", MethodNames.BIND_NAME));
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.ruleId, rule);
            scriptTexts.add(scriptText);
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atException(names, true);
            model.setHelper(TtlHelper.class);
            model.addAction(MustacheUtil.format("cleanBarrier({{0}});", MethodNames.BIND_NAME));
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.ruleId, rule);
            scriptTexts.add(scriptText);
        }
        BMUtil.submitText(scriptTexts);
    }
}
