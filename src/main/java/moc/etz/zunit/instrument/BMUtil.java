package moc.etz.zunit.instrument;


import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.tools.attach.AgentInitializationException;
import lombok.SneakyThrows;
import org.jboss.byteman.agent.install.Install;
import org.jboss.byteman.agent.submit.ScriptText;
import org.jboss.byteman.agent.submit.Submit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BMUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(BMUtil.class);
    public static void main(String[] args) {
        System.out.println(getPid());
    }
    private interface CLibrary extends Library {
        CLibrary INSTANCE =  Native.load("c", CLibrary.class);
        int getpid ();
    }
    public final static String AGENT_VERSION = "org.jboss.byteman.agent.version";

    public static String getHost() {
        return "localhost";
    }


    public static int getPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start byteman agent");
    }


    public static int getPid() {
        int pid;
        try {
            pid=Kernel32.INSTANCE.GetCurrentProcessId();
        } catch (Exception e) {
            pid= CLibrary.INSTANCE.getpid();
        }
        LOGGER.debug("CurrentProcessId="+pid);
        return pid;
    }

    public static void loadAgent() throws Exception {
        String id = String.valueOf(getPid());

        try {
            System.out.println("BMUnit : loading agent id = " + id);
            Properties properties = new Properties();
            properties.setProperty("org.jboss.byteman.transform.all", "true");
            properties.setProperty("org.jboss.byteman.debug", "true");
            Submit submit = new Submit(getHost(), getPort());
            int size = properties.size();
            String[] proparray = new String[size];
            int i = 0;
            for (String key : properties.stringPropertyNames()) {
                proparray[i++] = key + "=" + properties.getProperty(key);
            }
            Install.install(id, true, false, getHost(), getPort(), proparray);
            submit.setSystemProperties(properties);
        } catch (AgentInitializationException e) {
            // this probably indicates that the agent is already installed
        }


    }

    @SneakyThrows
    public static void submitFile(String btm) {
        Submit submit = new Submit(getHost(), getPort());
        List<String> files = new ArrayList<String>();
        files.add(btm);
        System.out.println("BMUnit : loading file script = " + btm);
        submit.addRulesFromFiles(files);
    }

    @SneakyThrows
    public static void unload(String btm) {
        Submit submit = new Submit(getHost(), getPort());
        List<String> files = new ArrayList<String>();
        files.add(btm);
        System.out.println("BMUnit : unloading file script = " + btm);
        submit.deleteRulesFromFiles(files);
    }


    @SneakyThrows
    public static void submitText(List<ScriptText> scripts) {
        Submit submit = new Submit(getHost(), getPort());
        submit.addScripts(scripts);
    }

    @SneakyThrows
    public static void unloadText(List<ScriptText> scripts) {
        Submit submit = new Submit(getHost(), getPort());
        submit.deleteScripts(scripts);
    }


}
