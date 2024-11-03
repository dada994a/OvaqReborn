package net.shoreline.client.impl.manager.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AntiDumpManager {

    public static void checkDebugger() {
        boolean isDebuggerAttached = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments()
                .toString().contains("-agentlib:jdwp");
        if (isDebuggerAttached) {
            showWarningAndExit("Debugger detected! The application will close.");
        }
    }

    public static void checkrecf() {
        String userHome = System.getProperty("user.home");
        String recafPath = userHome + File.separator + ".recaf" + File.separator + "recaf.jar";

        if (new File(recafPath).exists()) {
            showWarningAndExit("Recaf detected! The application will close.");
        }
    }

    public static void init() {
        checkDebugger();
        checkrecf();
    }
    private static void showWarningAndExit(String message) {
        UIManager.put("OptionPane.minimumSize", new Dimension(400, 100));
        JOptionPane.showMessageDialog(null, message, "AntiDump Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(1);
    }
}
