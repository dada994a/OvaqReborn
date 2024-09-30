package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.entity.player.PlayerMoveEvent; // こちらを修正
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.util.player.PlayerUtil;

public class BlockMoveModule extends ToggleModule {
    public BlockMoveModule() {
        super("BlockMove", "Allows phase-like movement along block edges", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onMove(final PlayerMoveEvent event) {
        if (MovementUtil.isInputtingMovement() && isPlayerOnEdge()) {
            double moveSpeed = 0.1; // Movement speed
            Vec3d playerPos = mc.player.getPos();

            // Calculate the new position based on input and movement speed
            Vec3d forward = mc.player.getRotationVector();
            double newX = playerPos.x + forward.x * moveSpeed;
            double newZ = playerPos.z + forward.z * moveSpeed;

            // Set the new position, allowing phase-like movement
            mc.player.setPos(newX, playerPos.y, newZ);
            event.cancel();
        }
    }

    private boolean isPlayerOnEdge() {
        BlockPos pos = PlayerUtil.getRoundedBlockPos(mc.player.getX(), mc.player.getY(), mc.player.getZ()).down();
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isReplaceable()) {
                return false; // エッジにいる場合
            }

        }
        return true;
    }
}



