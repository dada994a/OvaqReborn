package net.shoreline.client.impl.manager.client;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.util.List;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class AntiDumpManager {

    private static final String[] naughtyFlags = {
            "-XBootclasspath",
            "-javaagent",
            "-Xdebug",
            "-agentlib",
            "-agentlib:jdwp",
            "-Xrunjdwp",
            "-Xnoagent",
            "-verbose",
            "-DproxySet",
            "-DproxyHost",
            "-DproxyPort",
            "-Djavax.net.ssl.trustStore",
            "-Djavax.net.ssl.trustStorePassword"
    };

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void checkDebugger() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : inputArguments) {
            if (arg.matches(".*(-agentlib:jdwp|-Xdebug|-javaagent).*")) {
                showWarningAndExit("Debugger detected!");
            }
        }
    }

    public static void checkRecafAsync() {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isEmpty()) {
            return;
        }

        Path searchPath = Paths.get(userHome, "AppData", "Roaming");
        CompletableFuture.runAsync(() -> findForRecaf(searchPath), executor)
                .thenRun(executor::shutdown);
    }

    private static void findForRecaf(Path searchPath) {
        try (Stream<Path> paths = Files.find(searchPath, Integer.MAX_VALUE,
                (path, basicFileAttributes) -> path.getFileName().toString().toLowerCase().contains("recaf"))) {
            paths.findFirst()
                    .ifPresent(path -> showWarningAndExit("Recaf detected!"));
        } catch (Exception ignored) {
        }
    }

    public static void checkNaughtyFlags() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : naughtyFlags) {
            for (String inputArgument : inputArguments) {
                if (inputArgument.contains(arg)) {
                    showWarningAndExit("Found illegal program arguments!");
                }
            }
        }
    }

    public static void init() {
        checkDebugger();
        checkRecafAsync();
        checkNaughtyFlags();
    }

    private static void showWarningAndExit(String message) {
        UIManager.put("OptionPane.minimumSize", new Dimension(400, 100));
        JOptionPane.showMessageDialog(null, message, "Security Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(1);
    }
}
