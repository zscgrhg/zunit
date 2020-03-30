package moc.etz.zunit.instrument.wrapper;

import moc.etz.zunit.instrument.BMRuleMustacheModel;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.instrument.MethodNames;
import moc.etz.zunit.instrument.TtlHelper;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceBMWrapper implements TtlBMWrapper {
    @Override
    public void wrap() throws Exception {
        wrapSubmitCallable();
        wrapSubmitRunnable();
        wrapSubmitRunnableAndResult();
        wrapInvokeAll();
        wrapInvokeAllWithTimeout();
        wrapInvokeAny();
        wrapInvokeAnyWithTimeout();
    }

    public void wrapSubmitCallable() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method submitCallable = ExecutorService.class.getDeclaredMethod("submit", Callable.class);
        MethodNames names = MethodNames.build(submitCallable);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.submit(com.alibaba.ttl.TtlCallable.get($1));");
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

    public void wrapSubmitRunnableAndResult() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method submitRunnableAndResult = ExecutorService.class.getDeclaredMethod("submit", Runnable.class, Object.class);
        MethodNames names = MethodNames.build(submitRunnableAndResult);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.submit(com.alibaba.ttl.TtlRunnable.get($1),$2);");
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


    public void wrapSubmitRunnable() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method submitRunnable = ExecutorService.class.getDeclaredMethod("submit", Runnable.class);
        MethodNames names = MethodNames.build(submitRunnable);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.submit(com.alibaba.ttl.TtlRunnable.get($1));");
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

    public void wrapInvokeAllWithTimeout() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method invokeAllWithTimeout = ExecutorService.class.getDeclaredMethod("invokeAll", Collection.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(invokeAllWithTimeout);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.invokeAll(com.alibaba.ttl.TtlCallable.gets($1),$2,$3);");
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

    public void wrapInvokeAll() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method invokeAll = ExecutorService.class.getDeclaredMethod("invokeAll", Collection.class);
        MethodNames names = MethodNames.build(invokeAll);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.invokeAll(com.alibaba.ttl.TtlCallable.gets($1));");
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


    public void wrapInvokeAnyWithTimeout() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method invokeAnyWithTimeout = ExecutorService.class.getDeclaredMethod("invokeAny", Collection.class, long.class, TimeUnit.class);
        MethodNames names = MethodNames.build(invokeAnyWithTimeout);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.invokeAny(com.alibaba.ttl.TtlCallable.gets($1),$2,$3);");
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

    public void wrapInvokeAny() throws Exception {
        List<ScriptText> scriptTexts = new ArrayList<>();
        Method invokeAny = ExecutorService.class.getDeclaredMethod("invokeAny", Collection.class);
        MethodNames names = MethodNames.build(invokeAny);
        {
            BMRuleMustacheModel model = BMRuleMustacheModel.atEntry(names, true);
            model.setHelper(TtlHelper.class);
            model.setCondition(MustacheUtil.format("setBarrier({{0}})", MethodNames.BIND_NAME));
            model.addAction("return $0.invokeAny(com.alibaba.ttl.TtlCallable.gets($1));");
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
