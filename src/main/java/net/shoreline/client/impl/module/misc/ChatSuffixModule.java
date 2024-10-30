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
    private static final String LEMON_SUFFIX = "  \uD835\uDCC1ᴇᴍ\uD835\uDC5C\uD835\uDCC3";
    private static final String REBIRTH_SUFFIX = "  ✷ℜ\uD835\uDD22\uD835\uDD1F\uD835\uDD26\uD835\uDD2F\uD835\uDD31\uD835\uDD25";
    private static final String MOONBLADE_SUFFIX = "  ☽\uD835\uDD10\uD835\uDD2C\uD835\uDD2C\uD835\uDD2B\uD835\uDD1F\uD835\uDD29\uD835\uDD1E\uD835\uDD21\uD835\uDD22";
    private static final String MELON_SUFFIX = "  \uD835\uDD10\uD835\uDD22\uD835\uDD29\uD835\uDD2C\uD835\uDD2B\uD835\uDD05\uD835\uDD22\uD835\uDD31\uD835\uDD1E";
    private static final String MIO_SUFFIX = "  \uD835\uDDE0\uD835\uDDF6\uD835\uDDFC";
    private static final String ISOLATION_SUFFIX = " \u00a4\u0197\u0073\u00f8\u026d\u03b1\u0442\u0e40\u0e4f\u0e20C";

    private final Config<Mode> modeConfig = new EnumConfig<>("Mode", "The suffix mode to append to chat messages", Mode.OVAQ, Mode.values());

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
            case REBIRTH:
                suffix = REBIRTH_SUFFIX;
                break;
            case MOONBLADE:
                suffix = MOONBLADE_SUFFIX;
                break;
            case MELON:
                suffix = MELON_SUFFIX;
                break;
            case MIO:
                suffix = MIO_SUFFIX;
                break;
            case ISOLATION:
                suffix = ISOLATION_SUFFIX;
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
        OVAQ, CATMI, TEAM, DOT, LEMON, REBIRTH, MOONBLADE, MELON, MIO, ISOLATION
    }
}
