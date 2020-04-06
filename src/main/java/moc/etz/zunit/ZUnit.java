package moc.etz.zunit;

import com.alibaba.ttl.threadpool.agent.TtlAgent;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.VirtualMachine;
import lombok.SneakyThrows;
import moc.etz.zunit.instrument.BMUtil;
import moc.etz.zunit.parse.SubjectManager;
import moc.etz.zunit.trace.InvocationContext;
import moc.etz.zunit.util.LoggerUtil;
import org.jboss.byteman.agent.Main;
import shade.zunit.ch.qos.logback.classic.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class ZUnit {


    private static final Logger LOGGER = LoggerUtil.of(InvocationContext.class);

    public static final Properties CONFIG = loadConfig();

    @SneakyThrows
    public static Properties loadConfig() {
        Properties config = new Properties();
        config.load(ZUnit.class.getClassLoader().getResourceAsStream("zunit.properties"));
        return config;
    }


    public synchronized static void loadAgent() throws Exception {

        URL jnaLocation = Kernel32.class.getProtectionDomain().getCodeSource().getLocation();
        String path = Paths.get(jnaLocation.toURI()).toString();
        System.setProperty("jna.nosys", "true");
        System.setProperty("jna.boot.library.path", path);
        if (Main.firstTime) {
            BMUtil.loadAgent();
        }
        if (TtlAgent.firstLoad) {
            loadTtlAgent();
        }


        String subjectPkgs = CONFIG.getProperty("zunit.subject.pkg");
        Stream.of(subjectPkgs.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .forEach(pkg -> SubjectManager.getInstance().loadFromPkg(subjectPkgs));

    }

    @SneakyThrows
    public static void loadTtlAgent() {
        try {
            VirtualMachine jvm = VirtualMachine.attach(String.valueOf(BMUtil.getPid()));
            Path agentPath = Paths.get(TtlAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            jvm.loadAgent(agentPath.toString());
        } catch (AgentInitializationException e) {
            // this probably indicates that the agent is already installed
        }
    }

}
