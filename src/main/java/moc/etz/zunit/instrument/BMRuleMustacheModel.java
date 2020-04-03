package moc.etz.zunit.instrument;

import lombok.Data;
import moc.etz.zunit.util.MustacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static BMRuleMustacheModel with(MethodNames names, boolean override, String id) {

        String ruleId = id + names.symbol;
        BMRuleMustacheModel model = new BMRuleMustacheModel();
        model.ruleId = ruleId;
        String targetClass = MustacheUtil.format("{{0}} {{#1}}^{{/1}}{{2}}", names.contextType, override, names.ownerName);
        model.targetClass = targetClass;
        model.addBind(MustacheUtil.format("{{0}}:java.lang.Long=java.lang.Long.valueOf({{1}}L)", MethodNames.BIND_NAME, names.mid));
        model.targetMethod = names.erased;
        return model;
    }

    public static BMRuleMustacheModel atEntry(MethodNames names, boolean override) {

        BMRuleMustacheModel model = with(names, override, "entry:");
        model.location = "AT ENTRY";
        return model;
    }

    public static BMRuleMustacheModel atExit(MethodNames names, boolean override) {
        BMRuleMustacheModel model = with(names, override, "exit:");
        model.location = "AT EXIT";
        return model;
    }

    public static BMRuleMustacheModel atException(MethodNames names, boolean override) {
        BMRuleMustacheModel model = with(names, override, "exception:");
        model.location = "AT EXCEPTION EXIT";
        return model;
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
