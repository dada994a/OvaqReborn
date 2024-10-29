package net.shoreline.client.impl.module.combat;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.manager.combat.hole.Hole;
import net.shoreline.client.init.Managers;

public class HoleSnapModule extends ToggleModule {
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to snap to nearby holes", 1.0f, 3.0f, 8.0f);
    Config<Double> speedConfig = new NumberConfig<>("Speed", "The speed at which to snap to holes", 0.1, 1.0, 5.0);
    private BlockPos holePos;
    private Vec3d targetPos;
    private int stuckTicks;

    public HoleSnapModule() {
        super("HoleSnap", "Snaps player to nearby holes", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        holePos = null;
        stuckTicks = 0;
    }

    @Override
    public void onDisable() {
        this.holePos = null;
        this.stuckTicks = 0;
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (!mc.player.isAlive()) {
            disable();
            return;
        }

        Hole nearestHole = getNearestHole();
        if (nearestHole == null) {
            disable();
            return;
        }

        holePos = nearestHole.getPos();
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
        for (Hole hole : Managers.HOLE.getHoles()) {
            if (!hole.isStandard() && !hole.isDouble() && !hole.isQuad() && !hole.isDoubleX() && !hole.isDoubleZ()) {
                continue;
            }

            double dist = hole.squaredDistanceTo(mc.player);
            if (dist <= rangeConfig.getValue() * rangeConfig.getValue()) {
                return hole;
            }
        }
        return null;
    }
}
