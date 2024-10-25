package net.shoreline.client.impl.module.dev;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.player.InventoryUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class AntiAnvilModule extends ToggleModule {

    Config<Float> rangeConfig = new NumberConfig<>("Range", "number", 1.0f, 5.0f, 7.0f);
//TODO:動かないかもね(ゴリ押し)
    public AntiAnvilModule() {
        super("AntiAnvil", "Protect AutoAnvil", ModuleCategory.DEV);
    }

    @EventListener
    public void onTick() {
        for (int i = 0; i <= rangeConfig.getValue(); i++) {
            BlockPos pos = mc.player.getBlockPos().add(0, i + 3, 0);
            if (mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL && mc.world.getBlockState(pos.down()).isAir()) {
                if (InventoryUtil.hasItemInInventory(Items.OBSIDIAN, true)) {
                    placeBlock(pos.down());
                    break;
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (mc.player == null || mc.world == null) return;
        BlockHitResult hitResult = new BlockHitResult(mc.player.getPos(), Direction.UP, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);

    }
}
