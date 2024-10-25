package net.shoreline.client.impl.module.dev;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.client.input.Input;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class BlockMoveModule extends ToggleModule {

    Config<Boolean> middleConfig = new BooleanConfig("Middle", "Snap to middle of blocks", true);
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "Movement delay in milliseconds", 250, 0, 2000);
    Config<Boolean> onlyInBlockConfig = new BooleanConfig("Only In Block", "Move only when inside blocks", true);
    Config<Boolean> avoidOutConfig = new BooleanConfig("Avoid Out", "Avoid moving out of blocks", true, () -> !onlyInBlockConfig.getValue());

    private long lastMoveTime = 0;
    private final Vec3d[] sides = {
            new Vec3d(0.24D, 0.0D, 0.24D),
            new Vec3d(-0.24D, 0.0D, 0.24D),
            new Vec3d(0.24D, 0.0D, -0.24D),
            new Vec3d(-0.24D, 0.0D, -0.24D)
    };

    public BlockMoveModule() {
        super("BlockMove", "Allows movement through blocks while submerged", ModuleCategory.DEV);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (!mc.player.isSubmergedInWater()) return;

        Vec3d playerPos = mc.player.getPos();
        boolean isInAir = isPlayerInAir(playerPos);

        if (!isInAir && shouldMove()) {
            BlockPos targetPos = middleConfig.getValue() ? mc.player.getBlockPos() : new BlockPos((int) playerPos.getX(), (int) playerPos.getY(), (int) playerPos.getZ());


            Direction playerFacing = mc.player.getHorizontalFacing();

            Vec3d newPos = calculateNewPosition(targetPos, playerFacing, mc.player.input);
            if (newPos != null) {
                mc.player.updatePosition(newPos.x, newPos.y, newPos.z);
                lastMoveTime = System.currentTimeMillis();
            }

            mc.player.input.movementForward = 0;
            mc.player.input.movementSideways = 0;
        }
    }

    private boolean shouldMove() {
        return System.currentTimeMillis() - lastMoveTime > delayConfig.getValue();
    }

    private boolean isPlayerInAir(Vec3d playerPos) {
        for (Vec3d offset : sides) {
            BlockPos blockPos = new BlockPos((int) (playerPos.getX() + offset.x), (int) (playerPos.getY() + offset.y), (int) (playerPos.getZ() + offset.z));
            if (!mc.world.getBlockState(blockPos).isAir()) {
                Box box = mc.world.getBlockState(blockPos).getCollisionShape(mc.world, blockPos).getBoundingBox();
                if (box != null && mc.player.getBoundingBox().intersects(box)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Vec3d calculateNewPosition(BlockPos currentPos, Direction facing, Input input) {
        boolean isMovingX = Math.abs(facing.getOffsetX()) > 0;

        Vec3d newPos = null;
        if (input.pressingForward) {
            newPos = adjustPosition(currentPos, isMovingX, isMovingX ? facing.getOffsetX() < 0 : facing.getOffsetZ() < 0);
        } else if (input.pressingBack) {
            newPos = adjustPosition(currentPos, isMovingX, isMovingX ? facing.getOffsetX() > 0 : facing.getOffsetZ() > 0);
        } else if (input.pressingLeft) {
            newPos = adjustPosition(currentPos, !isMovingX, isMovingX ? facing.getOffsetX() > 0 : facing.getOffsetZ() > 0);
        } else if (input.pressingRight) {
            newPos = adjustPosition(currentPos, !isMovingX, isMovingX ? facing.getOffsetX() < 0 : facing.getOffsetZ() < 0);
        }

        return newPos;
    }

    private Vec3d adjustPosition(BlockPos pos, boolean isXDirection, boolean isNegative) {
        BlockPos newPos = isNegative ? pos.offset(isXDirection ? Direction.WEST : Direction.NORTH)
                : pos.offset(isXDirection ? Direction.EAST : Direction.SOUTH);
        return getValidPosition(newPos);
    }

    private Vec3d getValidPosition(BlockPos pos) {
        Vec3d newPos = middleConfig.getValue() ? new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)
                : new Vec3d(pos.getX(), pos.getY(), pos.getZ());
        boolean isValid = mc.world.getBlockState(pos).isAir() || mc.world.getBlockState(pos.up()).isAir();

        return (!onlyInBlockConfig.getValue() || isValid || !avoidOutConfig.getValue()) ? newPos : null;
    }
}
