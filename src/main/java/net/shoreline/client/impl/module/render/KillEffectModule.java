package net.shoreline.client.impl.module.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.entity.EntityDeathEvent;

/**
 * Credit https://github.com/moneymod/moneymod/blob/main/src/main/java/wtf/moneymod/client/impl/module/render/KillEffect.java
 *
 * @return
 */
public class KillEffectModule extends ToggleModule {

    Config<Effect> effectConfig = new EnumConfig<>("Effect", "The effect that is applied on kill", Effect.BOLT, KillEffectModule.Effect.values());
    Config<Boolean> soundConfig = new BooleanConfig("Sound", "Play sound on kill", true);

    public KillEffectModule() {
        super("KillEffect", "Spawns lightning and plays sound when you kill an enemy", ModuleCategory.RENDER);
    }
//エラーは出ないように調節したけど動かないかもね
//NaaNaaあとよろしくね
    @EventListener
    public void onEntityDeath(EntityDeathEvent event) {
        if (mc.player == null || event.getEntity() == null) return;

        Entity entity = event.getEntity();
        if (entity.isRemoved() || (entity instanceof PlayerEntity && ((PlayerEntity) entity).getHealth() <= 0)) {
            if (effectConfig.getValue() == Effect.BOLT) {
                mc.world.spawnEntity(new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world));
                if (soundConfig.getValue()) {
                    mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f, 1.0f);
                }
            }
        }
    }

    public enum Effect {
        BOLT
    }
}
