package moc.etz.zunit.instrument.wrapper;

import moc.etz.zunit.instrument.BMRuleMustacheModel;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.instrument.MethodNames;
import moc.etz.zunit.instrument.TtlHelper;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ExecutorBMWrapper implements TtlBMWrapper {

    @Override
    public void wrap() throws Exception {
        wrapExecuteMethod();
    }


    public void wrapExecuteMethod() throws Exception {
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
            ScriptText scriptText = new ScriptText(model.getRuleId(), rule);
            scriptTexts.add(scriptText);
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atExit(names, true);
            scriptTexts.add(BARRIER_CLEAN_FACTORY.apply(model));
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atException(names, true);
            scriptTexts.add(BARRIER_CLEAN_FACTORY.apply(model));
        }
        BMUtil.submitText(scriptTexts);
    }


}
