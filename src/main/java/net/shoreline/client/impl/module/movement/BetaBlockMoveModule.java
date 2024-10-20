package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.*;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.RunTickEvent;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class BetaBlockMoveModule extends ToggleModule {

    private static final double MAGIC_OFFSET = 0.200009968835369999878673424677777777777761;

    public BetaBlockMoveModule() {
        super("BetaBlockMoveModule", ":)", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


    @EventListener
    public void onGameTick(TickEvent event) {
        if (event.getStage() == EventStage.POST) {
            handleHitboxDesync();
        }
    }

    private void handleHitboxDesync() {
        Direction facing = mc.player.getHorizontalFacing();
        Box boundingBox = mc.player.getBoundingBox();
        Vec3d center = boundingBox.getCenter();
        Vec3d offset = new Vec3d(facing.getUnitVector());

        Vec3d blockEdgePosition = Vec3d.of(BlockPos.ofFloored(center))
                .add(0.5, 0, 0.5)
                .add(offset.multiply(0.5));

        Vec3d finalPosition = merge(blockEdgePosition.add(offset.multiply(MAGIC_OFFSET)), facing);
        mc.player.setPosition(finalPosition.x, mc.player.getY(), finalPosition.z);
    }

    @EventListener
    public void onBlockMoveTick(RunTickEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) {
            return;
        }

        Vec3d playerPos = mc.player.getPos();
        double moveSpeed = 0.1;

        Vec3d forward = mc.player.getRotationVector();

        if (mc.options.forwardKey.isPressed()) {
            mc.player.setPos(playerPos.x + forward.x * moveSpeed, playerPos.y, playerPos.z + forward.z * moveSpeed);
        }

        if (mc.options.backKey.isPressed()) {
            mc.player.setPos(playerPos.x - forward.x * moveSpeed, playerPos.y, playerPos.z - forward.z * moveSpeed);
        }

        Vec3d left = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d right = left.multiply(-1);

        if (mc.options.leftKey.isPressed()) {
            mc.player.setPos(playerPos.x + right.x * moveSpeed, playerPos.y, playerPos.z + right.z * moveSpeed);
        }

        if (mc.options.rightKey.isPressed()) {
            mc.player.setPos(playerPos.x - right.x * moveSpeed, playerPos.y, playerPos.z - right.z * moveSpeed);
        }
    }

    private Vec3d merge(Vec3d position, Direction facing) {
        return new Vec3d(
                position.x * Math.abs(facing.getUnitVector().x),
                position.y * Math.abs(facing.getUnitVector().y),
                position.z * Math.abs(facing.getUnitVector().z)
        );
    }
}
