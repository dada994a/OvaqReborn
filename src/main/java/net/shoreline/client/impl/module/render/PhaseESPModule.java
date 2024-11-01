package net.shoreline.client.impl.module.render;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.util.world.BlastResistantBlocks;

import java.awt.*;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class PhaseESPModule extends ToggleModule {
    Config<Boolean> safeConfig = new BooleanConfig("Safe", "Highlight", false);
    Config<Color> unsafeConfig = new ColorConfig("UnsafeColor", "The color for rendering unsafe phase blocks", new Color(255, 0, 0), false, false);
    Config<Color> obsidianConfig = new ColorConfig("ObsidianColor", "The color for rendering obsidian phase blocks", new Color(255, 255, 0), false, false, () -> this.safeConfig.getValue());
    Config<Color> bedrockConfig = new ColorConfig("BedrockColor", "The color for rendering bedrock phase blocks", new Color(0, 255, 0), false, false, () -> this.safeConfig.getValue());

    public PhaseESPModule() {
        super("PhaseESP", "pearl", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        BlockPos playerPos = mc.player.getBlockPos();
        for (Direction dir : Direction.values()) {
            BlockPos blockPos;
            if (mc.world.getBlockState(blockPos = playerPos.offset(dir)).isAir()) continue;
            Vec3d pos = mc.player.getPos();
            BlockState state = mc.world.getBlockState(blockPos);
            Color color = null;
            if (state.isAir()) {
                color = unsafeConfig.getValue();
            } else if (this.safeConfig.getValue()) {
                color = BlastResistantBlocks.isUnbreakable(state.getBlock()) ? this.bedrockConfig.getValue() : this.obsidianConfig.getValue();
            }
            if (color == null) continue;
            double x = blockPos.getX();
            double y = blockPos.getY();
            double z = blockPos.getZ();
            double dx = pos.x - playerPos.getX();
            double dz = pos.z - playerPos.getZ();
            if (dir == Direction.NORTH && dx >= 0.65) {
                RenderManager.drawLine(event.getMatrices(), x, y, z, x, y, z + 1.0, color.getRGB());
                continue;
            }
            if (dir == Direction.SOUTH && dx <= 0.35) {
                RenderManager.drawLine(event.getMatrices(), x + 1.0, y, z, x + 1.0, y, z + 1.0, color.getRGB());
                continue;
            }
            if (dir == Direction.EAST && dz >= 0.65) {
                RenderManager.drawLine(event.getMatrices(), x, y, z, x + 1.0, y, z, color.getRGB());
                continue;
            }
            if (dir != Direction.WEST || !(dz <= 0.35)) continue;
            RenderManager.drawLine(event.getMatrices(), x, y, z + 1.0, x + 1.0, y, z + 1.0, color.getRGB());
        }
    }
}
