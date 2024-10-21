package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.*;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.RunTickEvent;

public class BetaBlockMoveModule extends ToggleModule {

    private static final double MOVE_SPEED = 0.1;

    public BetaBlockMoveModule() {
        super("BetaBlockMoveModule", ":)", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            return;
        }

        Vec3d playerPos = mc.player.getPos();
        if (mc.options.forwardKey.isPressed()) {
            moveToEdge(playerPos, MOVE_SPEED);
        }
        if (mc.options.backKey.isPressed()) {
            moveToEdge(playerPos, -MOVE_SPEED);
        }

        Vec3d forward = mc.player.getRotationVector().normalize();
        Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d right = left.multiply(-1);

        if (mc.options.leftKey.isPressed()) {
            mc.player.setPos(playerPos.x + right.x * MOVE_SPEED, playerPos.y, playerPos.z + right.z * MOVE_SPEED);
        }
        if (mc.options.rightKey.isPressed()) {
            mc.player.setPos(playerPos.x - right.x * MOVE_SPEED, playerPos.y, playerPos.z - right.z * MOVE_SPEED);
        }
    }

    private void moveToEdge(Vec3d playerPos, double moveSpeed) {
        Vec3d forward = mc.player.getRotationVector().normalize();
        BlockPos blockPos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        double targetX = blockPos.getX() + (forward.x > 0 ? 1 : forward.x < 0 ? 0 : 0.5);
        double targetZ = blockPos.getZ() + (forward.z > 0 ? 1 : forward.z < 0 ? 0 : 0.5);
        mc.player.setPos(targetX, playerPos.y, targetZ);
    }
}