package net.shoreline.client.impl.module.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Formatting;
import net.shoreline.client.OvaqReborn;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.impl.event.network.SocketReceivedPacketEvent;
import net.shoreline.client.socket.SocketChat;
import net.shoreline.client.socket.SocketWebhookManager;
import net.shoreline.client.util.chat.ChatUtil;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }
        if (discordConfig.getValue() && receivediscordConfig.getValue()) {
            new Thread(() -> {
                try {
                    String myId = "1296263865327288403";

                    new Thread(() -> {
                        try {
                            URL url = new URL("https://discord.com/api/v9/channels/" + myId + "/messages?limit=1");
                            final String jsonResponse = getString(url);
                            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                                List<Message> messages = parseMessages(jsonResponse);
                                if (!messages.isEmpty() && !messages.get(0).id().equals(lastMessageId)) {
                                    lastMessageId = messages.get(0).id();
                                    ChatUtil.clientSendMessageRaw(Formatting.GRAY + "[" + Formatting.AQUA + "OvaqReborn" + Formatting.DARK_BLUE + "Discord" + Formatting.GRAY + "] " + Formatting.WHITE + Formatting.BOLD + messages.get(0).author() + ": " + messages.get(0).content() + Formatting.RESET);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                }
            }).start();
        }
    }

    private @NotNull String getString(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "MTI5NjYzNzgxNDk4NDY3NTQyMw.Gk9uJ7.QcH-4CWG484hqs3mIzSLtrwd0uN94XBE0HYrM8");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (IOException e) {
            if (con.getResponseCode() == 401) {
                OvaqReborn.LOGGER.error("Unauthorized: Invalid discord token");
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }
    }

    private List<Message> parseMessages(String jsonResponse) {
        List<Message> messages = new ArrayList<>();
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonResponse, JsonArray.class);
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            String id = jsonObject.get("id").getAsString();
            String content = jsonObject.get("content").getAsString();
            String author = jsonObject.get("author").getAsJsonObject().get("username").getAsString();
            messages.add(new Message(id, content, author));
        }
        return messages;
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        String message = event.getMessage();
        String prefix = "@";

        if (message.startsWith(prefix)) {
            String trimmedMessage = message.substring(prefix.length());
            if (trimmedMessage.length() < 150) {
                new Thread(() -> {
                    if (discordConfig.getValue()) {
                        SocketWebhookManager.send(mc.getSession().getUsername(), trimmedMessage);
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
            ChatUtil.clientSendMessageRaw(Formatting.GRAY + "[" + Formatting.AQUA + "OvaqReborn" + Formatting.GRAY + "] " + nickColor + Formatting.BOLD + nick + ": " + text + Formatting.RESET);
        }).start();
    }

    private record Message(String id, String content, String author) {}
}