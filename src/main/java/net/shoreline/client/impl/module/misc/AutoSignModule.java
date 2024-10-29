package net.shoreline.client.impl.module.misc;

import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AutoSignModule extends ToggleModule {
    private String[] text;

    public AutoSignModule() {
        super("AutoSign", "Automatically places sign.", ModuleCategory.MISC);
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String[] getText() {
        return this.text;
    }

    @Override
    public void onEnable() {
        ChatUtil.clientSendMessage(" §p[!] §fPlace down a sign to set text!");
        this.text = null;
    }
}
