package net.shoreline.client.impl.module.render;

import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.entity.FlagGetEvent;
import net.shoreline.client.util.math.timer.CacheTimer;

/**
 * AnimationsModule - Customizes swing and other animations.
 * Author: OvaqReborn
 * Since: 1.0
 */
public class AnimationsModule extends ToggleModule {
    Config<Boolean> swingSpeedConfig = new BooleanConfig("Swing Speed", "Controls swing speed", true);
    Config<Integer> speedConfig = new NumberConfig<>("Speed", "Set swing speed", 0, 1, 20, () -> swingSpeedConfig.getValue());
    Config<Boolean> offhandConfig = new BooleanConfig("Offhand Swing", "Swings offhand", true);
    Config<Boolean> sneakConfig = new BooleanConfig("Sneak", "Modifies sneak animation", true);

    private final CacheTimer swingTimer = new CacheTimer();
    private boolean isSwinging = false;

    public AnimationsModule() {
        super("Animations", "Customizes swing and other animations", ModuleCategory.RENDER);
    }

    @EventListener
    public void onTick() {
        if (swingSpeedConfig.getValue()) {
            if (swingTimer.passed(500 / speedConfig.getValue())) {
                mc.player.handSwinging = false;
                isSwinging = false;
                swingTimer.reset();
            }

            if (!isSwinging) {
                mc.player.swingHand(Hand.MAIN_HAND);
                isSwinging = true;
            }
        }

        if (offhandConfig.getValue() && mc.player.handSwinging) {
            mc.player.swingHand(Hand.OFF_HAND);
        }
    }

    @EventListener
    public void onFlagGet(FlagGetEvent event) {
        if (event.getEntity() instanceof PlayerEntity && event.getEntity() != mc.player && event.getFlag() == 0 && sneakConfig.getValue()) {
            event.setReturnValue(true);
        }
    }
}
