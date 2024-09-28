package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.util.chat.ChatUtil;

public class ChatSuffixModule extends ToggleModule {

    private final Config<String> suffixConfig = new StringConfig("Suffix", "Set custom chat suffix", "｜OvaqReborn");

    public ChatSuffixModule() {
        super("ChatSuffix", "Appends a custom suffix to all sent messages", ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {

        String originalMessage = event.getMessage();

        if (originalMessage.startsWith("/") || originalMessage.startsWith(".") || originalMessage.startsWith("#")) {
            return;
        }

        String newMessage = originalMessage + suffixConfig.getValue();

        ChatUtil.serverSendMessage(newMessage);

        event.cancel();
    }
}
