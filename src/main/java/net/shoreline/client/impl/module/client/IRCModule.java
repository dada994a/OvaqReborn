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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rom & Hypinohaizin
 */
public class IRCModule extends ToggleModule {
    public static SocketChat chat = new SocketChat("wss://hack.chat/chat-ws", "OvaqRebornChat", mc.getSession().getUsername(), "");
    Config<Boolean> discordConfig = new BooleanConfig("Discord", "make u lag when u send message. we will fix this later", false); // laggy maybe
    Config<Boolean> receivediscordConfig = new BooleanConfig("Receive Discord Message", "..", true);

    String[] devs = {
            "kisqra",
            "7wp5",
            "Naa_Naa"
    };
    Map<String, String> map = Stream.of(new String[][] {
            { "@hypinohaizin", "<@1143899002463588423>"},
            { "@rom", "<@904534958901723146>" },
            { "@naanaa", "<@1187645156338442250>" },
            { "@momo", "<@1258293421290815488>" },
            { "@taketyan", "<@1162993757285781624>" },
            { "@asupara", "<@1076669435022164109>" },
            { "@tikuwa", "<@1260213569723699210>" },
            { "@smoky", "<@875378998744580186>" },
            { "@ken", "<@1072488642817294389>" },
            { "@dacho", "<@1113048267136176169>" }
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private String lastMessageId = null;

    public IRCModule() {
        super("IRC", "global chat. prefix: @", ModuleCategory.CLIENT);
    }

    @Override
    public void onEnable() {
        ChatUtil.clientSendMessage("Connecting To IRC Server...");

        new Thread(() -> {
            chat.connect();
            ChatUtil.clientSendMessage("Connected!");
        }).start();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ChatUtil.clientSendMessage("Disconnected!");

        new Thread(() -> {
            chat.disconnect();
        }).start();
    }

    private String replaceMentions(String content) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        return content;
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        String message = event.getMessage();
        String prefix = "@";

        if (message.startsWith(prefix)) {
            String trimmedMessage = message.substring(prefix.length());
            if (trimmedMessage.length() < 150) {
                new Thread(() -> {
                    String finalTrimmedMessage = trimmedMessage;
                    if (discordConfig.getValue()) {
                        finalTrimmedMessage = replaceMentions(finalTrimmedMessage);
                        SocketWebhookManager.send(mc.getSession().getUsername(), finalTrimmedMessage);
                    }
                    chat.send(trimmedMessage);
                }).start();
            } else {
                ChatUtil.error("You cannot send more than 150 characters.");
            }
            event.cancel();
        }
    }

    @EventListener
    public void onSocketReceiveMessage(SocketReceivedPacketEvent event) {
        new Thread(() -> {
            String text = event.getText().length() < 150 ? event.getText() : Formatting.DARK_RED + "(long message)";
            String nick = event.getNick();

            boolean isDev = false;
            for (String dev : devs) {
                if (dev.equalsIgnoreCase(nick)) {
                    isDev = true;
                    break;
                }
            }
            Formatting nickColor = isDev ? Formatting.LIGHT_PURPLE : Formatting.WHITE;
            if (discordConfig.getValue() && receivediscordConfig.getValue() && nick.equalsIgnoreCase("Server"))
                ChatUtil.clientSendMessageRaw(Formatting.GRAY + "[" + Formatting.AQUA + "OvaqReborn" + Formatting.DARK_BLUE + "Discord" + Formatting.GRAY + "] " + nickColor + Formatting.BOLD + nick + ": " + text + Formatting.RESET);
            else ChatUtil.clientSendMessageRaw(Formatting.GRAY + "[" + Formatting.AQUA + "OvaqReborn" + Formatting.GRAY + "] " + nickColor + Formatting.BOLD + nick + ": " + text + Formatting.RESET);
        }).start();
    }
}