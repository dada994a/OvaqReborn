package net.shoreline.client.impl.module.combat;

import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;


public final class CevBreakerModule extends ToggleModule {

    public CevBreakerModule() {
        super("CevBreaker", "Places obsidian, interacts with a crystal, and breaks the obsidian", ModuleCategory.COMBAT);
        enable();
    }

    public void onTick() {
        if (mc.player == null || mc.interactionManager == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
        BlockPos targetPos = blockHitResult.getBlockPos().up();

        if (mc.world.getBlockState(targetPos).isAir() && mc.player.getMainHandStack().getItem() == Blocks.OBSIDIAN.asItem()) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
        }
        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN && mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
            mc.world.spawnEntity(new EndCrystalEntity(mc.world, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5));
        }

        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.OBSIDIAN) {
            mc.interactionManager.updateBlockBreakingProgress(targetPos, Direction.UP);
        }
    }
}
