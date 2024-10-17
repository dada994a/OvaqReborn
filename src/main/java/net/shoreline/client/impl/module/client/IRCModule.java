package net.shoreline.client.impl.module.client;

import net.minecraft.util.Formatting;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.impl.event.network.SocketReceivedPacketEvent;
import net.shoreline.client.socket.SocketChat;
import net.shoreline.client.socket.SocketWebhookManager;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author OvaqReborn
 */
public class IRCModule extends ToggleModule {
    SocketChat chat = new SocketChat("wss://hack.chat/chat-ws", "OvaqRebornChat", mc.getSession().getUsername(), "");

    Config<Boolean> discordConfig = new BooleanConfig("Discord", "make u lag when u send message. we will fix this later", false); //laggy maybe

    public IRCModule() {
        super("IRC", "global chat. prefix: @", ModuleCategory.CLIENT);
    }

    @Override
    public void onEnable() {
        ChatUtil.clientSendMessage("Connecting To IRC Server...");
        chat.connect();
        ChatUtil.clientSendMessage("Connected!");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ChatUtil.clientSendMessage("Disconnected!");
        chat.disconnect();
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        String message = event.getMessage();
        String prefix = "@";

        if (message.startsWith(prefix)) {
            String trimmedMessage = message.substring(prefix.length());
            if (trimmedMessage.length() < 150) {
                if (discordConfig.getValue())
                    SocketWebhookManager.send(mc.getSession().getUsername(), trimmedMessage);
                chat.send(trimmedMessage);
            } else {
                ChatUtil.error("You cannot send more than 150 characters.");
            }
            event.cancel();
        }
    }

    @EventListener
    public void onSocketReceiveMessage(SocketReceivedPacketEvent event) {
        String text = event.getText().length() < 150 ? event.getText() : Formatting.DARK_RED + "(long message)";
        String nick = event.getNick();

        ChatUtil.clientSendMessageRaw(Formatting.GRAY + "[" + Formatting.AQUA + "OvaqReborn" + Formatting.GRAY + "] " + Formatting.WHITE + Formatting.BOLD + nick + ": " + text + Formatting.RESET);
    }
}