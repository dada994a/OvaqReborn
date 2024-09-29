package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.RunTickEvent;
import net.shoreline.client.api.module.ModuleCategory;

public class BlockMoveModule extends ToggleModule {

    public BlockMoveModule() {
        super("BlockMove", "Allows movement through blocks while submerged", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            return;
        }

        Vec3d playerPos = mc.player.getPos();
        double moveSpeed = 0.1; // Move speed can be adjusted

        // Get the forward direction vector
        Vec3d forward = mc.player.getRotationVector();

        // Horizontal movement
        if (mc.options.forwardKey.isPressed()) {
            mc.player.setPos(playerPos.x + forward.x * moveSpeed, playerPos.y, playerPos.z + forward.z * moveSpeed);
        }
        if (mc.options.backKey.isPressed()) {
            mc.player.setPos(playerPos.x - forward.x * moveSpeed, playerPos.y, playerPos.z - forward.z * moveSpeed);
        }

        // Left and right movement
        Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d right = left.multiply(-1);

        if (mc.options.leftKey.isPressed()) {
            mc.player.setPos(playerPos.x + left.x * moveSpeed, playerPos.y, playerPos.z + left.z * moveSpeed);
        }
        if (mc.options.rightKey.isPressed()) {
            mc.player.setPos(playerPos.x + right.x * moveSpeed, playerPos.y, playerPos.z + right.z * moveSpeed);
        }

        // Vertical movement
        if (mc.options.jumpKey.isPressed()) {
            mc.player.setPos(playerPos.x, playerPos.y + moveSpeed, playerPos.z);
        }
        if (mc.options.sneakKey.isPressed()) {
            mc.player.setPos(playerPos.x, playerPos.y - moveSpeed, playerPos.z);
        }
    }
}
