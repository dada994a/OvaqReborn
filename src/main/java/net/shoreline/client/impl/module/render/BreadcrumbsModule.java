package net.shoreline.client.impl.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.util.render.VertexUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class BreadcrumbsModule extends ToggleModule {
    ArrayList<Vec3d> spots = new ArrayList<>();
    int ticks = 0;

    Config<Color> colorConfig = new ColorConfig("Color", "color for line", new Color(255, 0, 0), false, false);

    public BreadcrumbsModule() {
        super("Breadcrumbs", "Renders a line connecting all previous positions", ModuleCategory.RENDER);
    }

    @Override
    public void onDisable() {
        spots.clear();
    }

    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (mc.player != null) spots.add(mc.player.getPos());
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        ticks++;
        while (spots.size() > 20) {
            spots.remove(0);
        }
        MatrixStack stack = event.getMatrices();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(2);
        RenderManager.BUFFER.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        Vec3d lastPos = null;
        for (int i = 0; i < spots.size(); i++) {
            Vec3d spot = spots.get(i);
            if (lastPos != null) {
                float alpha = (float) i / spots.size();
                int lineColorValue = colorConfig.getValue().getRGB();
                Color lineColor = new Color(lineColorValue, true);
                Color color = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                        MathHelper.clamp((int) (255 * alpha), 0, 255));
                VertexUtil.vertexLine(stack, RenderManager.BUFFER,
                        (float) lastPos.x,
                        (float) lastPos.y,
                        (float) lastPos.z,
                        (float) spot.x,
                        (float) spot.y,
                        (float) spot.z,
                        color);
            }
            lastPos = spot;
        }
        RenderManager.TESSELLATOR.draw();
        RenderSystem.enableCull();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
}
