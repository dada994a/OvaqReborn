package net.shoreline.client.impl.module.combat;

import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

public class BedAuraModule extends ToggleModule { // Ensure this extends ToggleModule

    private final MinecraftClient client;
    private final ClientPlayerEntity player; // Change to ClientPlayerEntity

    public BedAuraModule() {
        super("BedAura", "Automatically Bedplace and explode", ModuleCategory.COMBAT);
        this.client = MinecraftClient.getInstance();
        this.player = client.player; // This will now be of type ClientPlayerEntity
    }

    public void onUpdate() {
        if (player == null || client.world == null) {
            return; // Ensure player and world are valid
        }

        // Check if player is sleeping in a bed
        if (player.isSleeping()) {
            return; // Don't activate if player is sleeping
        }

        BlockPos bedPos = findNearbyBed();

        if (bedPos != null) {
            attackBed(bedPos);
        }
    }

    private BlockPos findNearbyBed() {
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                BlockPos pos = player.getBlockPos().add(x, 0, z);
                if (client.world.getBlockState(pos).getBlock() instanceof BedBlock) {
                    return pos;
                }
            }
        }
        return null; // No bed found
    }

    private void attackBed(BlockPos bedPos) {
        if (client.world.canSetBlock(bedPos)) {
            // Create a BlockHitResult for interaction
            BlockHitResult hitResult = new BlockHitResult(
                    new Vec3d(bedPos.getX() + 0.5, bedPos.getY() + 0.5, bedPos.getZ() + 0.5),
                    Direction.UP,
                    bedPos,
                    false
            );

            // Use the player's hand to interact with the bed
            player.swingHand(Hand.MAIN_HAND);
            client.interactionManager.interactBlock(player, Hand.MAIN_HAND, hitResult); // Update here
        }
    }
}
