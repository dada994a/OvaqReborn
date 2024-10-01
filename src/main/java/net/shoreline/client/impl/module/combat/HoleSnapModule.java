package net.shoreline.client.impl.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.impl.manager.combat.hole.Hole;
import net.shoreline.client.init.Managers;

import java.util.ArrayList;
import java.util.List;

public class HoleSnapModule extends ToggleModule {
    Config<Float> rangeConfig = new NumberConfig<>("SnapRange", "The range to snap to nearby holes", 1.0f, 4.0f, 5.0f);
    Config<Boolean> proximityConfig = new BooleanConfig("ProximityCheck", "Snaps to holes when enemies are within a certain range", false);
    Config<Float> proximityRangeConfig = new NumberConfig<>("ProximityRange", "The range from the target to the hole", 0.5f, 1.0f, 4.0f, () -> proximityConfig.getValue());
    Config<Float> enemyRangeConfig = new NumberConfig<>("EnemyRange", "The maximum range of targets", 1.0f, 10.0f, 15.0f);
    private int snapDelay;

    public HoleSnapModule() {
        super("HoleSnap", "Snaps player to nearby holes", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onDisconnect(DisconnectEvent event) {
        disable();
    }

    @EventListener
    public void onPlayerTick(PlayerTickEvent event) {
        if (snapDelay > 0) {
            snapDelay--;
            return;
        }

        List<BlockPos> holesToSnap = new ArrayList<>();
        for (Hole hole : Managers.HOLE.getHoles()) {
            if (hole.squaredDistanceTo(mc.player) > rangeConfig.getValue()) {
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
                    if (player.getY() > hole.getY() && hole.squaredDistanceTo(player) > proximityRangeConfig.getValue()) {
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
        mc.player.setPos(targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5);
        snapDelay = 1;

        disable();
    }
}
