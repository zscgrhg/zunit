package moc.etz.zunit.trace;

import org.jboss.byteman.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvocationHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationHelper.class);
    Rule rule;

    public InvocationHelper(Rule rule) {
        this.rule = rule;
    }

    public void printTrigger() {
        LOGGER.debug(rule.getName());
    }
}
