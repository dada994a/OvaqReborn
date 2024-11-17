package net.shoreline.client.impl.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.gui.hud.RenderOverlayEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.util.math.ProjectionUtil;
import net.shoreline.client.util.render.VertexUtil;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class PearlESPModule extends ToggleModule {
    List<object> objects = new ArrayList<>();

    Config<Color> colorConfig = new ColorConfig("Color", "color for line", new Color(255, 0, 0), false, false);
    Config<Boolean> textureConfig = new BooleanConfig("Texture", "a", true);
    Config<Boolean> textConfig = new BooleanConfig("Text", "text for pearl esp", true);
    Config<Boolean> timeConfig = new BooleanConfig("Time", "time for text display", true, () -> textConfig.getValue());

    public PearlESPModule() {
        super("PearlESP", "thrown entity", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderOverlayPost(RenderOverlayEvent.Post event) {
        if (mc.world == null) {
            return;
        }
        DrawContext context = event.getContext();
        MatrixStack stack = context.getMatrices();

        for (object object : objects) {
            Vec3d pos = object.position;
            Vector2f projection = ProjectionUtil.project(pos.x, pos.y - 0.3F, pos.z);
            int ticks = object.ticks;
            if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
                continue;
            }
            double time = ticks * 50 / 1000.0;
            String text = String.format("%.1f", time);
            float textWidth = RenderManager.tf.getStringWidth(text) + 11;
            float posX = projection.x + textWidth / 2 - 6;
            float posY = projection.y - 8;
            if (textConfig.getValue()) {
                if (timeConfig.getValue()) {
                    RenderManager.tf.drawString(
                            stack,
                            text,
                            posX + 1.2,
                            posY + 1.4,
                            -1
                    );
                }
            }
            if (textureConfig.getValue()) {
                stack.push();
                stack.translate(posX - 1, posY - 9.5, 0);
                stack.scale(0.5F, 0.5F, 0);
                context.drawItem(new ItemStack(Items.ENDER_PEARL), 0, -5);
                stack.translate(-posX - 1, -posY - 9.5, 0);
                stack.pop();
            }
        }
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        if (mc.world == null) {
            return;
        }
        MatrixStack stack = event.getMatrices();
        stack.push();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(2);
//
        RenderManager.BUFFER.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        objects.clear();
        int lineColorValue = colorConfig.getValue().getRGB();
        Color lineColor = new Color(lineColorValue, true);
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity enderPearlEntity) {
                Vec3d motion = enderPearlEntity.getVelocity();
                Vec3d pos = enderPearlEntity.getPos();
                Vec3d prevPos;
                int ticks = 0;
                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);
                    motion = getMotion(enderPearlEntity, prevPos, motion);
                    HitResult hitResult = mc.world.raycast(
                            new RaycastContext(prevPos, pos,
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    enderPearlEntity));
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        pos = hitResult.getPos();
                    }
                    float alpha = i / 25.0f;
                    Color color = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                            MathHelper.clamp((int) (255 * alpha), 0, 255));
                    VertexUtil.vertexLine(stack, RenderManager.BUFFER, (float) prevPos.x,
                            (float) prevPos.y,
                            (float) prevPos.z,
                            (float) pos.x,
                            (float) pos.y,
                            (float) pos.z,
                            color);
                    if (hitResult.getType() == HitResult.Type.BLOCK || pos.y < -128) {
                        objects.add(new object(pos, ticks));
                        break;
                    }
                    ticks++;
                }
            }
        }
        RenderManager.TESSELLATOR.draw();
        RenderSystem.enableCull();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stack.pop();
    }

    Vec3d getMotion(ThrownEntity throwable, Vec3d prevPos, Vec3d motion) {
        boolean isInWater = mc.world.getBlockState(BlockPos.ofFloored(prevPos))
                .getFluidState()
                .isIn(FluidTags.WATER);

        if (isInWater) {
            motion = motion.multiply(0.8);
        } else {
            motion = motion.multiply(0.99);
        }

        if (!throwable.hasNoGravity()) {
            motion = motion.add(0, -0.03F, 0);
        }
        return motion;
    }

    record object(Vec3d position, int ticks) {}
}
