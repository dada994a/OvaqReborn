package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.util.chat.ChatUtil;

public class ChatSuffixModule extends ToggleModule {
    private static final String OVAQ_SUFFIX = " ｜ ᴏᴠᴀǫʀᴇʙᴏʀɴ ᴅᴇᴠ";
    private static final String CATMI_SUFFIX = " ᴄᴀᴛᴍɪ";
    private static final String TEAM_SUFFIX = " ｜ ᴛᴇᴀᴍ 2ᴘ2ꜰᴊᴘ";
    private static final String DOT_SUFFIX = " ᴅᴏᴛɢᴏᴅ";
    private static final String LEMON_SUFFIX =" \uD835\uDCC1ᴇᴍ\uD835\uDC5C\uD835\uDCC3";
    private final Config<Mode> modeConfig = new EnumConfig<>("SuffixMode", "The suffix mode to append to chat messages", Mode.OVAQ, Mode.values());

    public ChatSuffixModule() {
        super("ChatSuffix", "Appends Suffix to all sent messages", ModuleCategory.MISC);
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        String originalMessage = event.getMessage();

        if (originalMessage.contains("/") || originalMessage.contains(".") || originalMessage.contains("#") || originalMessage.contains("@")) {
            return;
        }
        String suffix;
        switch (modeConfig.getValue()) {
            case CATMI:
                suffix = CATMI_SUFFIX;
                break;
            case TEAM:
                suffix = TEAM_SUFFIX;
                break;
            case DOT:
                suffix = DOT_SUFFIX;
                break;
            case LEMON:
                suffix = LEMON_SUFFIX;
                break;
            case OVAQ:
            default:
                suffix = OVAQ_SUFFIX;
                break;
        }

        String newMessage = originalMessage + suffix;
        ChatUtil.serverSendMessage(newMessage);
        event.cancel();
    }
    public enum Mode {
        OVAQ, CATMI, TEAM, DOT, LEMON
    }
}
