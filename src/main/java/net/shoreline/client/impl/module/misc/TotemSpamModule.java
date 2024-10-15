package net.shoreline.client.impl.module.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;

public class TotemSpamModule extends ToggleModule {

    private final Config<String> totemMessageConfig = new StringConfig("SpamMessage", "Message to send when a player pops a totem", "{name} totem割れてますよｗ");
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private long keepAliveId;

    public TotemSpamModule() {
        super("TotemSpam", "Sends a message when a player pops a totem.", ModuleCategory.MISC);
    }
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (event.getPacket() instanceof KeepAliveS2CPacket packet) {
            keepAliveId = packet.getId();
        }
//Fixd PlayerName
        else if (event.getPacket() instanceof EntityStatusS2CPacket packet
                && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING) {
            Entity entity = packet.getEntity(mc.world);
            if (entity instanceof PlayerEntity player && player != mc.player) {
                if (player.getDisplayName() != null && !Managers.SOCIAL.isFriend(player.getDisplayName())) {
                    String popMessage = totemMessageConfig.getValue().replace("{name}", player.getName().getString());
                    ChatUtil.serverSendMessage(popMessage);
                }
            }
        }
    }
}


