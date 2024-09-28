package net.shoreline.client.util.chat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shoreline.client.util.Globals;

public class ChatUtil implements Globals {
    private static final String PREFIX = "§s[OvaqReborn] §f";

    public static void clientSendMessage(String message) {
        if (mc != null && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.of(PREFIX + message), null, null);
        }
        }


    public static void clientSendMessage(String message, Object... params) {
        clientSendMessage(String.format(message, params));
    }

    public static void clientSendMessageRaw(String message) {
        if (mc != null && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.of(message), null, null);
        }
    }

    public static void clientSendMessageRaw(String message, Object... params) {
        clientSendMessageRaw(String.format(message, params));
    }

    public static void serverSendMessage(String message) {
        if (mc.player != null) {
            mc.player.networkHandler.sendChatMessage(message);
        }
    }

    public static void serverSendMessage(PlayerEntity player, String message) {
        if (mc.player != null) {
            String reply = "/msg " + player.getName().getString() + " ";
            mc.player.networkHandler.sendChatMessage(reply + message);
        }
    }

    public static void serverSendMessage(PlayerEntity player, String message, Object... params) {
        serverSendMessage(player, String.format(message, params));
    }

    public static void error(String message) {
        clientSendMessage(Formatting.RED + message);
    }

    public static void error(String message, Object... params) {
        clientSendMessage(Formatting.RED + message, params);
    }
}
