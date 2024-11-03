package net.shoreline.client;

import net.shoreline.client.api.Identifiable;
import net.shoreline.client.api.event.handler.EventBus;
import net.shoreline.client.api.event.handler.EventHandler;
import net.shoreline.client.api.file.ClientConfiguration;
import net.shoreline.client.impl.manager.client.AntiDumpManager;
import net.shoreline.client.impl.manager.client.DiscordManager;
import net.shoreline.client.impl.manager.client.HwidManager;
import net.shoreline.client.impl.module.client.IRCModule;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.swing.*;

public class OvaqReborn {
    public static Logger LOGGER;
    public static EventHandler EVENT_HANDLER;
    public static ClientConfiguration CONFIG;
    public static DiscordManager RPC;
    public static ShutdownHook SHUTDOWN;
    public static AntiDumpManager ANTIDUMP;
    public static Executor EXECUTOR;

    public static void logAsciiArt() {
        String asciiArt =
                "  ___                   ____      _                      \n" +
                        " / _ \\__   ____ _  __ _|  _ \\ ___| |__   ___  _ __ _ __  \n" +
                        "| | | \\ \\ / / _` |/ _` | |_) / _ \\ '_ \\ / _ \\| '__| '_ \\ \n" +
                        "| |_| |\\ V / (_| | (_| |  _ <  __/ |_) | (_) | |  | | | |\n" +
                        " \\___/  \\_/ \\__,_|\\__, |_| \\_\\___|_.__/ \\___/|_|  |_| |_|\n" +
                        "                   |___/                                \n";

        LOGGER.info(asciiArt);
    }

    public static void init() {
        LOGGER = LogManager.getLogger("OvaqReborn");
        logAsciiArt();

        hwidAuth();
        info("HwidAuth successful!");
        ANTIDUMP = new AntiDumpManager();
        AntiDumpManager.checkDebugger();
        AntiDumpManager.checkRecaf();
        AntiDumpManager.checkNaughtyFlags();

        IOUtil.Init();

        info("preInit starting ...");

        EXECUTOR = Executors.newFixedThreadPool(1);
        EVENT_HANDLER = new EventBus();
        info("init starting ...");
        Managers.init();
        Modules.init();
        RPC = new DiscordManager();
        DiscordManager.startRPC();
        info("discordrpc starting ...");
        info("postInit starting ...");
        CONFIG = new ClientConfiguration();
        Managers.postInit();
        SHUTDOWN = new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(SHUTDOWN);
        CONFIG.loadClient();

        if (Modules.IRC.isEnabled() && !IRCModule.chat.isConnected()) {
            try {
                InetAddress address = InetAddress.getByName("www.google.com");
                boolean isReachable = address.isReachable(5000);
                if (isReachable) {
                    info("Connecting to IRC Server (Init)");
                    IRCModule.chat.connect();
                } else {
                    info("Network Connection Error. (Maybe Bug");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        info("Made by hypinohaizin,rom(nelf),Naa_Naa");
        info("OvaqReborn Load is done.");
    }
    // TODO: OvaqHwidAuthSystem
    public static void hwidAuth() {
        String hwid = HwidManager.getHWID();
        String url = "https://pastebin.com/raw/AtsAtG0Y";

        try (InputStream in = new URL(url).openStream();
             InputStreamReader inputStreamReader = new InputStreamReader(in);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String response = bufferedReader.lines().collect(Collectors.joining("\n"));

            if (!response.contains(hwid)) {
                UIManager.put("OptionPane.minimumSize", new Dimension(500, 150));
                JFrame frame = new JFrame();
                frame.setAlwaysOnTop(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JTextField hwidField = new JTextField(hwid);
                hwidField.setEditable(false);
                hwidField.setBackground(null);
                hwidField.setBorder(null);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(new JLabel("下にあるHwidをコピーしてハイピの廃人に送ってください"), BorderLayout.NORTH);
                panel.add(hwidField, BorderLayout.CENTER);
                panel.add(new JLabel("注意: これは初回起動時に表示されます"), BorderLayout.SOUTH);

                JOptionPane.showMessageDialog(frame, panel, "OvaqReborn HwidAuthSystem", JOptionPane.INFORMATION_MESSAGE);

                throw new SecurityException("Hwid認証に失敗しました。強制終了します。");
            }

        } catch (IOException e) {
            throw new RuntimeException("認証サーバーに接続できませんでした。強制終了します。", e);
        }
    }



    public static void info(String message) {
        LOGGER.info(String.format("[OvaqReborn] %s", message));
    }

    public static void info(String message, Object... params) {
        LOGGER.info(String.format("[OvaqReborn] %s", message), params);
    }

    public static void info(Identifiable feature, String message) {
        LOGGER.info(String.format("[%s] %s", feature.getId(), message));
    }

    public static void info(Identifiable feature, String message, Object... params) {
        LOGGER.info(String.format("[%s] %s", feature.getId(), message), params);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Object... params) {
        LOGGER.error(String.format(message, params));
    }

    public static void error(Identifiable feature, String message) {
        LOGGER.error(String.format("[%s] %s", feature.getId(), message));
    }

    public static void error(Identifiable feature, String message, Object... params) {
        LOGGER.error(String.format("[%s] %s", feature.getId(), message), params);
    }
}
