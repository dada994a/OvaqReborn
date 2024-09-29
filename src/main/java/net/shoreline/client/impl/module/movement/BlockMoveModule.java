package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.RunTickEvent;
import net.shoreline.client.api.event.listener.EventListener;

public class BlockMoveModule extends ToggleModule {

    public BlockMoveModule() {
        super("BlockMove", "Move freely through blocks when inside them", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        if (mc.player == null) return;

        // プレイヤーがブロックに埋まっている場合
        if (!mc.player.noClip && mc.player.isInsideWall()) {
            Vec3d playerPos = mc.player.getPos();
            float yaw = mc.player.getYaw(); // プレイヤーの視点のYaw（方位）

            // プレイヤーの進行方向ベクトルを計算
            Vec3d forward = new Vec3d(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));

            // 移動量を設定
            double moveSpeed = 0.1;

            // 前方向の移動
            if (mc.options.forwardKey.isPressed()) {
                mc.player.setPos(playerPos.x + forward.x * moveSpeed, playerPos.y, playerPos.z + forward.z * moveSpeed);
            }

            // 後方向の移動
            if (mc.options.backKey.isPressed()) {
                mc.player.setPos(playerPos.x - forward.x * moveSpeed, playerPos.y, playerPos.z - forward.z * moveSpeed);
            }

            // 左方向の移動
            if (mc.options.leftKey.isPressed()) {
                Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
                mc.player.setPos(playerPos.x + left.x * moveSpeed, playerPos.y, playerPos.z + left.z * moveSpeed);
            }

            // 右方向の移動
            if (mc.options.rightKey.isPressed()) {
                Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize().negate();
                mc.player.setPos(playerPos.x + right.x * moveSpeed, playerPos.y, playerPos.z + right.z * moveSpeed);
            }

        }
    }
}
