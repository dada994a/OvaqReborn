package net.shoreline.client.security;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AntiDump {

    private static final String[] naughtyFlags = {
            "-XBootclasspath",
            "-javaagent",
            "-Xdebug",
            "-agentlib",
            "-Xrunjdwp",
            "-Xnoagent",
            "-verbose",
            "-DproxySet",
            "-DproxyHost",
            "-DproxyPort",
            "-Djavax.net.ssl.trustStore",
            "-Djavax.net.ssl.trustStorePassword"
    };

    public static void checkDebugger() {
        boolean isDebuggerAttached = ManagementFactory.getRuntimeMXBean().getInputArguments()
                .toString().contains("-agentlib:jdwp");
        if (isDebuggerAttached) {
            showWarningAndExit("Debugger detected! The application will close.");
        }
    }

    public static void checkRecaf() {
        String userHome = System.getProperty("user.home");
        String recafPath = userHome + File.separator + ".recaf" + File.separator + "recaf.jar";

        if (new File(recafPath).exists()) {
            showWarningAndExit("Recaf detected! The application will close.");
        }
    }

    public static void checkNaughtyFlags() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String arg : naughtyFlags) {
            for (String inputArgument : inputArguments) {
                if (inputArgument.contains(arg)) {
                    showWarningAndExit("Found illegal program arguments! The application will close.");
                }
            }
        }
    }

    public static void init() {
        checkDebugger();
        checkRecaf();
        checkNaughtyFlags();
    }

    private static void showWarningAndExit(String message) {
        UIManager.put("OptionPane.minimumSize", new Dimension(400, 100));
        JOptionPane.showMessageDialog(null, message, "AntiDump Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(1);
    }
}
