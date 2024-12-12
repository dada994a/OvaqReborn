package net.shoreline.client.impl.module.combat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.manager.combat.hole.Hole;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;

public class HoleSnapModule extends ToggleModule {
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to snap to nearby holes", 1.0f, 3.0f, 8.0f);
    Config<Double> speedConfig = new NumberConfig<>("Speed", "The speed at which to snap to holes", 0.1, 0.1, 5.0);
    Config<Boolean> stepConfig = new BooleanConfig("Step","STEP!!", false);
    private Hole targetHole;
    private Vec3d targetPos;
    private int stuckTicks;


    public HoleSnapModule() {
        super("HoleSnap", "Snaps player to a nearby hole", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        if (stepConfig.getValue()) {
            Modules.STEP.enable();
        }
        targetHole = getNearestHole();
        stuckTicks = 0;

        if (targetHole == null) {
            disable();
        }
    }

    @Override
    public void onDisable() {
        if (Modules.STEP.isEnabled()) {
            Modules.STEP.disable();
        }
        targetHole = null;
        stuckTicks = 0;
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (!mc.player.isAlive() || targetHole == null) {
            disable();
            return;
        }

        BlockPos holePos = targetHole.getPos();
        Vec3d playerPos = mc.player.getPos();
        targetPos = new Vec3d(holePos.getX() + 0.5, playerPos.y, holePos.getZ() + 0.5);

        double distance = playerPos.distanceTo(targetPos);
        double cappedSpeed = Math.min(speedConfig.getValue(), distance);

        float yaw = (float) Math.toDegrees(Math.atan2(targetPos.z - playerPos.z, targetPos.x - playerPos.x)) - 90;
        double x = -(float) Math.sin(Math.toRadians(yaw)) * cappedSpeed;
        double z = (float) Math.cos(Math.toRadians(yaw)) * cappedSpeed;

        mc.player.setPosition(playerPos.x + x, playerPos.y, playerPos.z + z);

        if (distance < 0.1) {
            disable();
        }

        if (mc.player.horizontalCollision) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        if (stuckTicks > 4) {
            disable();
        }
    }

    private Hole getNearestHole() {
        Hole nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Hole hole : Managers.HOLE.getHoles()) {
            if (!hole.isStandard() && !hole.isDouble() && !hole.isQuad() && !hole.isDoubleX() && !hole.isDoubleZ()) {
                continue;
            }

            double dist = hole.squaredDistanceTo(mc.player);
            if (dist <= rangeConfig.getValue() * rangeConfig.getValue() && dist < nearestDistance) {
                nearest = hole;
                nearestDistance = dist;
            }
        }
        return nearest;
    }
}
