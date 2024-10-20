package net.shoreline.client.impl.module.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.entity.EntityDeathEvent;

public class AutoEzModule extends ToggleModule {

    Config<String> messageConfig = new StringConfig("Message", "Message to send when a player dies", "OvaqReborn On top!");

    public AutoEzModule() {
        super("AutoEz", "Automatically sends a message when a player dies", ModuleCategory.MISC);
    }

    @EventListener
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            String message = messageConfig.getValue();
            mc.player.sendMessage(Text.of(message), false);
        }
    }
}
