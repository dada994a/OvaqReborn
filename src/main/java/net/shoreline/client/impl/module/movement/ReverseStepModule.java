package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.Box;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.entity.player.PlayerMoveEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.math.timer.CacheTimer;
import net.shoreline.client.util.math.timer.Timer;

/**
 * @author linus
 * @since 1.0
 */
public class ReverseStepModule extends ToggleModule {

    // Height setting
    Config<Float> heightConfig = new NumberConfig<>("Height", "The maximum fall height", 1.0f, 3.0f, 10.0f);

    private boolean prevOnGround;
    private final Timer groundTimer = new CacheTimer();

    public ReverseStepModule() {
        super("ReverseStep", "Falls down blocks faster", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        prevOnGround = false;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() == EventStage.PRE) {
            if (mc.player.isRiding()
                    || mc.player.isHoldingOntoLadder()
                    || mc.player.isInLava()
                    || mc.player.isTouchingWater()
                    || mc.player.input.jumping
                    || mc.player.input.sneaking) {
                return;
            }
            if (Modules.SPEED.isEnabled() || Modules.LONG_JUMP.isEnabled()
                    || Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled()) {
                return;
            }

            if (mc.player.isOnGround()) {
                prevOnGround = true;
                groundTimer.reset();
            }

            if (prevOnGround && groundTimer.passed(100) && isNearestBlockWithinHeight(heightConfig.getValue())) {
                Managers.MOVEMENT.setMotionY(-3.0);
            }
        }
    }

    @EventListener
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Modules.FLIGHT.isEnabled() || Modules.PACKET_FLY.isEnabled()) {
            return;
        }
        if (mc.player.getVelocity().y < 0 && prevOnGround && !mc.player.isOnGround()
                && isNearestBlockWithinHeight(heightConfig.getValue() + 0.01)) {
            event.setX(0.0);
            event.setZ(0.0);
            Managers.MOVEMENT.setMotionXZ(0.0, 0.0);
        }
    }

    private boolean isNearestBlockWithinHeight(double height) {
        Box bb = mc.player.getBoundingBox();
        for (double i = 0; i < height + 0.5; i += 0.01) {
            if (!mc.world.isSpaceEmpty(mc.player, bb.offset(0, -i, 0))) {
                return true;
            }
        }
        return false;
    }
}
