package net.shoreline.client.impl.manager.client;

import net.minecraft.client.MinecraftClient;
import net.shoreline.client.util.discord.DiscordEventHandlers;
import net.shoreline.client.util.discord.DiscordRPC;
import net.shoreline.client.util.discord.DiscordRichPresence;

public class DiscordManager {
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
    private static DiscordRichPresence presence = new DiscordRichPresence();
    private static boolean started = false;
    private static Thread rpcThread;

    public static void startRPC() {
        if (started) return;

        started = true;
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("1245335041781796947", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageText = "OvaqReborn";

        rpcThread = new Thread(() -> {
            while (started) {
                rpc.Discord_RunCallbacks();
                updatePresence();
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException ignored) {
                }
            }
        }, "logo");
        rpcThread.start();
    }

    private static void updatePresence() {
        presence.state = getServerInfo();
        rpc.Discord_UpdatePresence(presence);
    }



    private static String getServerInfo() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentServerEntry() != null) {
            return "Playing on " + mc.getCurrentServerEntry().address;
        } else if (mc.isInSingleplayer()) {
            return "Playing in Singleplayer";
        } else {
            return "In Title Menu";
        }
    }



    public void stopRPC() {
        started = false;
        if (rpcThread != null && !rpcThread.isInterrupted()) {
            rpcThread.interrupt();
        }
        rpc.Discord_Shutdown();
    }
}
