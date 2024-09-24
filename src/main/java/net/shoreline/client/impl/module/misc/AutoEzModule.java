package net.shoreline.client.impl.module.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.util.chat.ChatUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AutoEzModule extends ToggleModule {
    private final Config<Boolean> totemPopMessagesEnabled = new BooleanConfig("TotemPop Messages", "Send messages when enemies pop a totem", true);
    private final Config<String> totemPopMessages = new StringConfig("TotemPop Messages", "Message to send when an enemy pops a totem", "Totem Popd By <NAME>");

    private final Config<Boolean> dieMessagesEnabled = new BooleanConfig("Kill Messages", "Send messages when enemies die", true);
    private final Config<String> dieMessages = new StringConfig("Kill Messages", "Message to send when killing an enemy", "Killd By <NAME>");

    private final Random random = new Random();
    private final Map<UUID, Long> attackHistory = new HashMap<>();

    public AutoEzModule() {
        super("AutoEz", "Sends messages after an enemy dies or pops a totem.", ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
            PlayerEntity player = (PlayerEntity) packet.getEntity(mc.world);
            if (player == null) return;

            if (!isRecentlyAttackedByMe(player.getUuid())) return;

            if (packet.getStatus() == 35 && totemPopMessagesEnabled.getValue()) {
                sendMessage(totemPopMessages.getValue(), player.getName().getString());
            }

            if (packet.getStatus() == 3 && dieMessagesEnabled.getValue()) {
                sendMessage(dieMessages.getValue(), player.getName().getString());
            }
        }
    }

    private boolean isRecentlyAttackedByMe(UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        return attackHistory.containsKey(playerUUID) && (currentTime - attackHistory.get(playerUUID)) <= 10000;
    }

    public void recordAttack(PlayerEntity player) {
        attackHistory.put(player.getUuid(), System.currentTimeMillis());
    }

    private void sendMessage(String messageConfig, String playerName) {
        String message = messageConfig.replace("<NAME>", playerName);
        ChatUtil.serverSendMessage(message);
    }
}
