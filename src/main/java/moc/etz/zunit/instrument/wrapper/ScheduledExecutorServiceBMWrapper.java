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
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceBMWrapper implements TtlBMWrapper {
    @Override
    public void wrap() throws Exception {
        wrapScheduleAtFixedRate();
        wrapScheduleCallableWithDelay();
        wrapScheduleRunnableWithDelay();
        wrapScheduleWithFixedDelay();
    }

    public void wrapScheduleRunnableWithDelay() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method scheduleRunnableWithDelay = ScheduledExecutorService.class.getDeclaredMethod("schedule", Runnable.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(scheduleRunnableWithDelay);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.schedule(com.alibaba.ttl.TtlRunnable.get($1),$2,$3);");
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

    public void wrapScheduleCallableWithDelay() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method scheduleCallableWithDelay = ScheduledExecutorService.class.getDeclaredMethod("schedule", Callable.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(scheduleCallableWithDelay);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.schedule(com.alibaba.ttl.TtlCallable.get($1),$2,$3);");
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


    public void wrapScheduleAtFixedRate() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method scheduleAtFixedRate = ScheduledExecutorService.class.getDeclaredMethod("scheduleAtFixedRate", Runnable.class, long.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(scheduleAtFixedRate);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.scheduleAtFixedRate(com.alibaba.ttl.TtlRunnable.get($1),$2,$3,$4);");
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

    public void wrapScheduleWithFixedDelay() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method scheduleWithFixedDelay = ScheduledExecutorService.class.getDeclaredMethod("scheduleWithFixedDelay", Runnable.class, long.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(scheduleWithFixedDelay);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.scheduleWithFixedDelay(com.alibaba.ttl.TtlRunnable.get($1),$2,$3,$4);");
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
