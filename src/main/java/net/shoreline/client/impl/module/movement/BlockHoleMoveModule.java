package net.shoreline.client.impl.module.movement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.RunTickEvent;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.manager.combat.hole.Hole;
import net.shoreline.client.init.Managers;

import java.util.ArrayList;
import java.util.List;

public class BlockHoleMoveModule extends ToggleModule {
    // Snap settings
    private Config<Float> rangeConfig = new NumberConfig<>("SnapRange", "The range to snap to nearby holes", 1.0f, 4.0f, 5.0f);
    private Config<Boolean> proximityConfig = new BooleanConfig("ProximityCheck", "Snaps to holes when enemies are within a certain range", false);
    private Config<Float> proximityRangeConfig = new NumberConfig<>("ProximityRange", "The range from the target to the hole", 0.5f, 1.0f, 4.0f, () -> proximityConfig.getValue());
    private Config<Float> enemyRangeConfig = new NumberConfig<>("EnemyRange", "The maximum range of targets", 1.0f, 10.0f, 15.0f);

    private int snapDelay;

    public BlockHoleMoveModule() {
        super("BlockHoleMove", "Allows movement through blocks and snaps to nearby holes", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        disable();
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            return;
        }

        // Movement
        Vec3d playerPos = mc.player.getPos();
        double moveSpeed = 0.01;

        Vec3d forward = mc.player.getRotationVector();
        if (mc.options.forwardKey.isPressed()) {
            mc.player.setPos(playerPos.x + forward.x * moveSpeed, playerPos.y, playerPos.z + forward.z * moveSpeed);
        }
        if (mc.options.backKey.isPressed()) {
            mc.player.setPos(playerPos.x - forward.x * moveSpeed, playerPos.y, playerPos.z - forward.z * moveSpeed);
        }

        Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize(); // 左方向
        Vec3d right = left.multiply(-1);
        if (mc.options.leftKey.isPressed()) {
            mc.player.setPos(playerPos.x + right.x * moveSpeed, playerPos.y, playerPos.z + right.z * moveSpeed); // 右に移動
        }
        if (mc.options.rightKey.isPressed()) {
            mc.player.setPos(playerPos.x - right.x * moveSpeed, playerPos.y, playerPos.z - right.z * moveSpeed); // 左に移動
        }

        // Hole snapping
        if (snapDelay > 0) {
            snapDelay--;
            return;
        }

        List<BlockPos> holesToSnap = new ArrayList<>();
        for (Hole hole : Managers.HOLE.getHoles()) {
            if (hole.squaredDistanceTo(mc.player) > rangeConfig.getValue() * rangeConfig.getValue()) {
                continue;
            }

            if (proximityConfig.getValue()) {
                for (Entity entity : mc.world.getEntities()) {
                    if (!(entity instanceof PlayerEntity player) || player == mc.player) {
                        continue;
                    }
                    double dist = mc.player.distanceTo(player);
                    if (dist > enemyRangeConfig.getValue()) {
                        continue;
                    }
                    if (player.getY() > hole.getY() && hole.squaredDistanceTo(player) > proximityRangeConfig.getValue() * proximityRangeConfig.getValue()) {
                        continue;
                    }
                    holesToSnap.add(hole.getPos());
                    break;
                }
            } else {
                holesToSnap.add(hole.getPos());
            }
        }

        if (holesToSnap.isEmpty()) {
            return;
        }

        BlockPos targetPos = holesToSnap.get(0);
        mc.player.setPos(targetPos.getX() + 0.1, targetPos.getY() + 0.1, targetPos.getZ() + 0.1);
        snapDelay = 1;

        disable();
    }
}
