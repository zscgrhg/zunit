package moc.etz.zunit.instrument.wrapper;

import moc.etz.zunit.instrument.BMRuleMustacheModel;
import moc.etz.zunit.instrument.MethodNames;
import moc.etz.zunit.instrument.TtlHelper;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;

import java.util.function.Function;

public interface TtlBMWrapper {


    Function<BMRuleMustacheModel, ScriptText> BARRIER_CLEAN_FACTORY = (model -> {
        model.setHelper(TtlHelper.class);
        model.addAction(MustacheUtil.format("cleanBarrier({{0}});", MethodNames.BIND_NAME));
        String rule = MustacheUtil.render(model);
        ScriptText scriptText = new ScriptText(model.getRuleId(), rule);
        return scriptText;
    });

    void wrap() throws Exception;
}
