package net.shoreline.client.impl.module.world;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.impl.event.entity.player.PlayerMoveEvent;
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.util.player.PlayerUtil;

public final class SafeWalkModule extends Module {
    private boolean enabled;

    public SafeWalkModule() {
        super("SafeWalk", "Prevents you from falling off edges", ModuleCategory.WORLD);
        this.enabled = true; // Default to enabled
    }

    public void toggle() {
        enabled = !enabled;
    }

    @EventListener
    public void onMove(final PlayerMoveEvent event) {
        if (enabled && MovementUtil.isInputtingMovement() && mc.player.isOnGround()) {
            Vec3d movement = Vec3d.fromPolar(MovementUtil.applySafewalk(event.getX(), event.getZ()));

            // Calculate the safe movement
            event.setX(movement.x);
            event.setZ(movement.y);
            event.cancel();
        }
    }

    private boolean isPlayerOnEdge() {
        BlockPos pos = PlayerUtil.getRoundedBlockPos(mc.player.getX(), mc.player.getY(), mc.player.getZ()).down();
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.offset(direction);
            if (!mc.world.getBlockState(neighbor).isReplaceable()) {
                return false;
            }
        }
        return true;
    }
}
