package net.shoreline.client.impl.module.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.player.InventoryUtil;
import net.shoreline.client.impl.event.TickEvent;

import java.util.Comparator;

public class AutoAnvilModule extends ToggleModule {

    private PlayerEntity target;
    private int timer;
//TODO:動きません！
    Config<Float> rangeConfig = new NumberConfig<>("Range", "number", 1.0f, 5.0f, 7.0f);
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "ticks", 0, 20, 100);
    Config<Integer> heightConfig = new NumberConfig<>("Height", "blocks", 2, 5, 10);

    public AutoAnvilModule() {
        super("AutoAnvil", "Automatically places anvils above players", ModuleCategory.COMBAT);
    }

    public void onActivate() {
        timer = 0;
        target = null;
    }

    @EventListener
    public void onTick() {
        if (target == null || target.isDead() || mc.player.distanceTo(target) > rangeConfig.getValue()) {
            target = getTargetPlayer();
        }

        if (target == null || mc.player.distanceTo(target) > rangeConfig.getValue()) {
            return;
        }

        if (timer >= delayConfig.getValue()) {
            timer = 0;

            BlockPos targetPos = target.getBlockPos().up();
            for (int i = 0; i < heightConfig.getValue(); i++) {
                BlockPos placePos = targetPos.up(i);
                if (mc.world.getBlockState(placePos).isAir() && mc.world.getBlockState(placePos.down()).isReplaceable()) {
                    if (InventoryUtil.hasItemInInventory(Items.ANVIL, true)) {
                        BlockHitResult hitResult = new BlockHitResult(new Vec3d(placePos.getX(), placePos.getY(), placePos.getZ()), Direction.UP, placePos, false
                        );
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
                    }
                }
            }
        } else {
            timer++;
        }
    }

    private PlayerEntity getTargetPlayer() {
        return mc.world.getEntitiesByClass(PlayerEntity.class, mc.player.getBoundingBox().expand(rangeConfig.getValue()), player -> !player.equals(mc.player) && player.isAlive())
                .stream()
                .min(Comparator.comparingDouble(p -> mc.player.squaredDistanceTo(p)))
                .orElse(null);
    }
}
