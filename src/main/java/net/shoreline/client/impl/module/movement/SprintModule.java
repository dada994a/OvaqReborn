package net.shoreline.client.impl.module.movement;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.SprintCancelEvent;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class SprintModule extends RotationModule {

    Config<SprintMode> modeConfig = new EnumConfig<>("Mode", "Sprinting mode. Rage allows for multi-directional sprinting.", SprintMode.LEGIT, SprintMode.values());
    Config<Boolean> directionspoofConfig = new BooleanConfig("DirectionSpoof", "Face movement direction silently", false);

    public SprintModule() {
        super("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT);
    }

    @Override
    public String getModuleData() {
        return modeConfig.getValue().name();
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }

        if (MovementUtil.isInputtingMovement()
                && !mc.player.isSneaking()
                && !mc.player.isRiding()
                && !mc.player.isTouchingWater()
                && !mc.player.isInLava()
                && !mc.player.isHoldingOntoLadder()
                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && mc.player.getHungerManager().getFoodLevel() > 6.0F) {

            switch (modeConfig.getValue()) {
                case LEGIT -> {
                    if (mc.player.input.hasForwardMovement()
                            && (!mc.player.horizontalCollision
                            || mc.player.collidedSoftly)) {
                        mc.player.setSprinting(true);
                    }
                }
                case RAGE -> mc.player.setSprinting(true);
            }

            if (directionspoofConfig.getValue()) {
                Input input = mc.player.input;
                float rotationYaw = mc.player.getYaw();
                float yawOffset = MovementUtil.getYawOffset(input, rotationYaw);
                setRotation(yawOffset, mc.player.getPitch());
            }
        }
    }

    @EventListener
    public void onSprintCancel(SprintCancelEvent event) {
        if (MovementUtil.isInputtingMovement()
                && !mc.player.isSneaking()
                && !mc.player.isRiding()
                && !mc.player.isTouchingWater()
                && !mc.player.isInLava()
                && !mc.player.isHoldingOntoLadder()
                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && mc.player.getHungerManager().getFoodLevel() > 6.0F) {
            event.cancel();
        }
    }

    public enum SprintMode {
        LEGIT,
        RAGE
    }
}
