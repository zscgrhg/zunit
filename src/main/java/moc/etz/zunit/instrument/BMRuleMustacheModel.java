package moc.etz.zunit.instrument;

import lombok.Data;
import moc.etz.zunit.targets.ServiceA;
import moc.etz.zunit.targets.ServiceAImpl;
import moc.etz.zunit.trace.InvocationHelper;
import moc.etz.zunit.util.MustacheUtil;
import org.jboss.byteman.agent.submit.ScriptText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
public class BMRuleMustacheModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(BMRuleMustacheModel.class);
    final List<String> bind = new ArrayList<>();
    final List<String> action = new ArrayList<>();
    String ruleId;
    String targetClass;
    String helperClass;
    String targetMethod;
    String location;
    String condition = "true";

    private static BMRuleMustacheModel with(Method method, boolean override, String id) {
        MethodNames names = new MethodNames(method);
        String ruleId = id + names.symbol;
        BMRuleMustacheModel model = new BMRuleMustacheModel();
        model.ruleId = ruleId;
        String targetClass = MustacheUtil.format("{{0}} {{#1}}^{{/1}}{{2}}", names.ownerType, override, names.ownerName);
        model.targetClass = targetClass;
        model.targetMethod = names.signature;
        model.helperClass = "aaa";
        return model;
    }

    public static BMRuleMustacheModel atEntry(Method method, boolean override) {
        BMRuleMustacheModel model = with(method, override, "entry:");
        model.location = "AT ENTRY";
        return model;
    }

    public static BMRuleMustacheModel atExit(Method method, boolean override) {
        BMRuleMustacheModel model = with(method, override, "exit:");
        model.location = "AT EXIT";
        return model;
    }

    public static BMRuleMustacheModel atException(Method method, boolean override) {
        BMRuleMustacheModel model = with(method, override, "exception:");
        model.location = "AT EXCEPTION EXIT";
        return model;
    }

    public static void main(String[] args) throws Exception {
        BMUtil.loadAgent();
        Method[] methods = ServiceA.class.getDeclaredMethods();
        List<ScriptText> scriptTexts = new ArrayList<>();
        for (Method method : methods) {
            BMRuleMustacheModel model = atEntry(method, true);
            model.setHelper(InvocationHelper.class);
            model.addBind("info:java.lang.String=\"sss\";");
            model.addAction("printTrigger();");
            String rule = MustacheUtil.render(model);
            ScriptText scriptText = new ScriptText(model.ruleId, rule);
            scriptTexts.add(scriptText);
        }
        BMUtil.submitText(scriptTexts);
        ServiceA serviceA = new ServiceAImpl();
        serviceA.hello(null);
    }

    public boolean hasBind() {
        return !bind.isEmpty();
    }

    public boolean hasAction() {
        return !action.isEmpty();
    }

    public BMRuleMustacheModel setHelper(Class clazz) {
        this.helperClass = clazz.getName();
        return this;
    }

    public BMRuleMustacheModel addBind(String bind) {
        this.bind.add(bind);
        return this;
    }

    public BMRuleMustacheModel addAction(String act) {
        this.action.add(act);
        return this;
    }
}
