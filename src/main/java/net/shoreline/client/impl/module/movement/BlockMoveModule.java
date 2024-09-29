package net.shoreline.client.impl.module.movement;

import net.minecraft.util.math.Vec3d;
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

        if (!mc.player.noClip && mc.player.isInsideWall()) {
            Vec3d playerPos = mc.player.getPos();

            if (mc.options.leftKey.isPressed()) {
                mc.player.setPos(playerPos.x - 0.1, playerPos.y, playerPos.z);
            }

            if (mc.options.rightKey.isPressed()) {
                mc.player.setPos(playerPos.x + 0.1, playerPos.y, playerPos.z);
            }

            if (mc.options.forwardKey.isPressed()) {
                mc.player.setPos(playerPos.x, playerPos.y, playerPos.z + 0.1);
            }

            if (mc.options.backKey.isPressed()) {
                mc.player.setPos(playerPos.x, playerPos.y, playerPos.z - 0.1);
            }

            if (mc.options.jumpKey.isPressed()) {
                mc.player.setPos(playerPos.x, playerPos.y + 0.1, playerPos.z);
            }

            if (mc.options.sneakKey.isPressed()) {
                mc.player.setPos(playerPos.x, playerPos.y - 0.1, playerPos.z);
            }
        }
    }
}
