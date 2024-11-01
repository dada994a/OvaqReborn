package net.shoreline.client.impl.module.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.BuildConfig;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.DeathEvent;
import net.shoreline.client.api.event.TotemEvent;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;


import java.util.Random;

public class AutoEzModule extends ToggleModule {

   // Config<Boolean> killConfig = new BooleanConfig("Kill Message", "Send a message when a player is killed", true);
   // Config<Boolean> popConfig = new BooleanConfig("Pop Message", "Send a message when a player pops a totem", true);
    Config<MessageMode> messageModeConfig = new EnumConfig<>("Mode", "The mode for the rotation yaw spin ", MessageMode.DEFAULT, MessageMode.values());
    Config<String> customMessageConfig = new StringConfig("Custom Message", "Custom message format", "GG %player%");
    Config<Integer> rangeConfig = new NumberConfig<>("Range", "Range to detect players", 10, 10, 50);

    private final Random random = new Random();
    private static final String[] defaultKillMessages = {
            "%player% was slain by OvaqReborn" + BuildConfig.VERSION,
            "GGs %player%!"
    };
    private static final String[] PopMessages = {
            "%player% was slain by OvaqReborn" + BuildConfig.VERSION,
            "GGs %player%!"
    };



    public AutoEzModule() {
        super("AutoEz", "Sends a message when a player dies or pops a totem", ModuleCategory.MISC);
    }

/*
    @EventListener
    public void onPlayerDeath(DeathEvent event) {
        PlayerEntity player = event.getPlayer();
        if (shouldSendMessage(player)) {
            String message = generateMessage(player);
            sendMessage(message);
        }
    }
    @EventListener
    public void onPlayerTotemPop(TotemEvent event) {
        PlayerEntity player = event.getPlayer();
        if (shouldSendMessage(player)) {
            String message = generateMessage(player);
            sendMessage(message);
        }
    }
     */

    private boolean shouldSendMessage(PlayerEntity player) {
        return !mc.player.equals(player) && mc.player.distanceTo(player) <= rangeConfig.getValue();
    }

    private String generateMessage(PlayerEntity player) {
        if (messageModeConfig.getValue() == MessageMode.CUSTOM) {
            return customMessageConfig.getValue().replace("%player%", player.getName().getString());
        } else {
            int index = MathHelper.clamp(random.nextInt(defaultKillMessages.length), 0, defaultKillMessages.length - 1);
            return defaultKillMessages[index].replace("%player%", player.getName().getString());
        }
    }

    private void sendMessage(String message) {
        mc.player.networkHandler.sendChatMessage(message);
    }

    public enum MessageMode {
        CUSTOM,
        DEFAULT
    }
}
