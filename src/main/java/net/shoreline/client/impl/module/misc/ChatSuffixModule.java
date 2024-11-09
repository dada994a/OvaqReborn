package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.gui.chat.ChatMessageEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;

public class ChatSuffixModule extends ToggleModule {
    private static final String OVAQ_SUFFIX = " ｜ ᴏᴠᴀǫʀᴇʙᴏʀɴ ᴅᴇᴠ";
    private static final String CATMI_SUFFIX = " ᴄᴀᴛᴍɪ";
    private static final String TEAM_SUFFIX = " ｜ ᴛᴇᴀᴍ 2ᴘ2ꜰᴊᴘ";
    private static final String DOT_SUFFIX = " ᴅᴏᴛɢᴏᴅ";
    private static final String DARE_SUFFIX = " ｜ お前誰やねん";
    private static final String REVO_SUFFIX = "|レグルス連邦 Team Revolution(ChatGPTクラン)";
    private final Config<Mode> modeConfig = new EnumConfig<>("Mode", "The suffix mode to append to chat messages", Mode.OVAQ, Mode.values());

    public ChatSuffixModule() {
        super("ChatSuffix", "Appends Suffix to all sent messages", ModuleCategory.MISC);
    }

    @EventListener
    public void onChatMessage(ChatMessageEvent.Client event) {
        String originalMessage = event.getMessage();
        String commandPrefix = Managers.COMMAND.getPrefix();

        if (originalMessage.isEmpty() ||  (originalMessage.contains("/") || originalMessage.contains("#") || originalMessage.contains("@")|| originalMessage.startsWith(commandPrefix))) {
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
            case DARE:
                suffix = DARE_SUFFIX;
                break;
            case GOMI:
                suffix = REVO_SUFFIX;
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
        OVAQ, CATMI, TEAM, DOT, DARE, GOMI
    }
}
