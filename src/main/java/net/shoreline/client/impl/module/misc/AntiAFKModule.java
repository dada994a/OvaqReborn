package net.shoreline.client.impl.module.misc;

import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author linus
 * @since 1.0
 */
public class AntiAFKModule extends ToggleModule {
    //
    Config<Boolean> autoReplyConfig = new BooleanConfig("AutoReply", "Replies to players messaging you in chat", true);
    Config<String> replyConfig = new StringConfig("Reply", "The reply message for AutoReply", "[OvaqReborn] I am currently AFK.");

    /**
     *
     */
    public AntiAFKModule() {
        super("AntiAFK", "Prevents the player from being kicked for AFK",
                ModuleCategory.MISC);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof ChatMessageS2CPacket packet
                && autoReplyConfig.getValue()) {
            String[] words = packet.body().content().split(" ");
            if (words[1].startsWith("whispers:")) {
                ChatUtil.serverSendMessage("/r " + replyConfig.getValue());
            }
        }
    }
}
