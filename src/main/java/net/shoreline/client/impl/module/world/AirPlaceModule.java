package net.shoreline.client.impl.module.world;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;

/**
 * AirPlaceModule allows players to place blocks where their crosshair is pointing, using a fixed range.
 */
public class AirPlaceModule extends ToggleModule {

    private final Config<Double> rangeConfig = new NumberConfig<>("Range", "Custom range.", 1.0, 1.0, 6.0);
    private HitResult hitResult;

    public AirPlaceModule() {
        super("AirPlace", "Allows you to place a block where your crosshair is pointing.", ModuleCategory.WORLD);
    }

    @EventListener
    public void onTick(TickEvent event) {
        double range = rangeConfig.getValue();
        hitResult = mc.getCameraEntity().raycast(range, 0, false);

        if (!(hitResult instanceof BlockHitResult blockHitResult) || !(mc.player.getMainHandStack().getItem() instanceof BlockItem) && !(mc.player.getMainHandStack().getItem() instanceof SpawnEggItem)) {
            return;
        }

        if (mc.options.useKey.isPressed()) {
            placeBlock(blockHitResult.getBlockPos());
        }
    }

    private void placeBlock(BlockPos pos) {
        if (mc.world.isAir(pos) && isBlockAccessible(pos)) {
            ItemStack itemStack = mc.player.getMainHandStack();
            ItemUsageContext context = new ItemUsageContext(mc.player, Hand.MAIN_HAND, (BlockHitResult) hitResult);
            itemStack.useOnBlock(context);
        }
    }

    private boolean isBlockAccessible(BlockPos pos) {
        World world = mc.world;
        return world.isAir(pos) && !world.isAir(pos.down())
                && world.isAir(pos.up()) && world.isAir(pos.up(2));
    }
}
