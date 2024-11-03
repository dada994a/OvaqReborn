package net.shoreline.client.impl.manager.client;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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
                showWarningAndExit("Debugger detected! The application will close.");
            }
        }
    }

    public static void checkRecafAsync() {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isEmpty()) {
            return;
        }

        Path searchPath = Paths.get(userHome, "AppData", "Roaming");
        CompletableFuture.runAsync(() -> searchForRecaf(searchPath), executor)
                .thenRun(executor::shutdown);
    }

    private static void searchForRecaf(Path searchPath) {
        try (Stream<Path> paths = Files.walk(searchPath)) {
            paths.filter(Files::exists)
                    .filter(path -> path.getFileName().toString().toLowerCase().contains("recaf"))
                    .findFirst()
                    .ifPresent(path -> showWarningAndExit("Recaf detected at " + path + "! The application will close."));
        } catch (Exception ignored) {
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
        checkRecafAsync();
        checkNaughtyFlags();
    }

    private static void showWarningAndExit(String message) {
        UIManager.put("OptionPane.minimumSize", new Dimension(400, 100));
        JOptionPane.showMessageDialog(null, message, "Security Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(1);
    }
}
