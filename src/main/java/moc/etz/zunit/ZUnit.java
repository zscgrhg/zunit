package moc.etz.zunit;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import com.sun.tools.attach.VirtualMachine;
import lombok.SneakyThrows;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.parse.SubjectManager;
import moc.etz.zunit.trace.InvocationContext;
import org.jboss.byteman.agent.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class ZUnit {


    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationContext.class);

    public static final Properties CONFIG = loadConfig();

    @SneakyThrows
    public static Properties loadConfig() {
        Properties config = new Properties();
        config.load(ZUnit.class.getClassLoader().getResourceAsStream("zunit.properties"));
        return config;
    }


    public synchronized static void loadAgent() throws Exception {
        if (Main.firstTime) {
            BMUtil.loadAgent();
        }
        if (!TtlAgent.isTtlAgentLoaded()) {
            loadAlitababa();
        }


        String subjectPkgs = CONFIG.getProperty("zunit.subject.pkg");
        Stream.of(subjectPkgs.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .forEach(pkg -> SubjectManager.getInstance().loadFromPkg(subjectPkgs));

    }

    @SneakyThrows
    public static void loadAlitababa() {
        VirtualMachine jvm = VirtualMachine.attach(String.valueOf(BMUtil.getPid()));
        Path agentPath = Paths.get(TtlAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        jvm.loadAgent(agentPath.toString());
    }

}
