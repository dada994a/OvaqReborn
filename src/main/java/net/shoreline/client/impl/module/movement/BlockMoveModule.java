package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.module.ModuleCategory; // 追加
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.RunTickEvent;

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

        // 左右の移動
        Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize(); // 左方向
        Vec3d right = left.multiply(-1); // 右方向は左の逆

        if (mc.options.leftKey.isPressed()) {
            mc.player.setPos(playerPos.x - left.x * moveSpeed, playerPos.y, playerPos.z - left.z * moveSpeed); // 左に移動
        }
        if (mc.options.rightKey.isPressed()) {
            mc.player.setPos(playerPos.x + left.x * moveSpeed, playerPos.y, playerPos.z + left.z * moveSpeed); // 右に移動
        }

        // 縦の移動
        if (mc.options.jumpKey.isPressed()) {
            mc.player.setPos(playerPos.x, playerPos.y + moveSpeed, playerPos.z);
        }
        if (mc.options.sneakKey.isPressed()) {
            mc.player.setPos(playerPos.x, playerPos.y - moveSpeed, playerPos.z);
        }
    }
}
