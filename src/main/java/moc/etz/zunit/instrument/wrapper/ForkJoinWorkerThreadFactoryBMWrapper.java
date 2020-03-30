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
import java.util.concurrent.ForkJoinPool;


public class ForkJoinWorkerThreadFactoryBMWrapper implements TtlBMWrapper {

    @Override
    public void wrap() throws Exception {
        wrapNewThread();
    }

    public void wrapNewThread() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method newThread = ForkJoinPool.ForkJoinWorkerThreadFactory.class.getDeclaredMethod("newThread", ForkJoinPool.class);
        MethodNames names = MethodNames.build(newThread);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("backupTtl();");
            model.addAction("return $0.newThread($1);");
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.getRuleId(), rule);
            scriptTexts.add(scriptText);
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atExit(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("cleanBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("restoreTtl();");
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.getRuleId(), rule);
            scriptTexts.add(scriptText);
        }
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atException(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("cleanBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("restoreTtl();");
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.getRuleId(), rule);
            scriptTexts.add(scriptText);
        }
        BMUtil.submitText(scriptTexts);
    }
}
