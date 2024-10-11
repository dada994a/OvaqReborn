package net.shoreline.client.impl.module.misc;

import net.shoreline.client.BuildConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.util.chat.ChatUtil;

public class ChatSuffixModule extends ToggleModule {
    private static final String SUFFIX = " ｜ ᴏᴠᴀǫʀᴇʙᴏʀɴ ᴅᴇᴠ" ;
    //    private static final String SUFFIX = " ｜ ᴏᴠᴀǫʀᴇʙᴏʀɴ" ;

    public ChatSuffixModule() {
        super("ChatSuffix", "Appends Suffix to all sent messages", ModuleCategory.MISC);
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {

        String originalMessage = event.getMessage();

        if (originalMessage.contains("/") || originalMessage.contains(".") || originalMessage.contains("#")) {
            return;
        }
// こんな感じ
        String newMessage = originalMessage + SUFFIX;

        ChatUtil.serverSendMessage(newMessage);

        event.cancel();
    }
}