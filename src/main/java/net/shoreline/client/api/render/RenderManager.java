package net.shoreline.client.api.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.shoreline.client.impl.font.TTFFontRenderer;
import net.shoreline.client.init.Fonts;
import net.shoreline.client.init.Modules;
import net.shoreline.client.mixin.accessor.AccessorWorldRenderer;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.math.MathUtil;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.shoreline.client.api.render.RenderBuffers.LINES;
import static net.shoreline.client.api.render.RenderBuffers.QUADS;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class RenderManager implements Globals {
    public static final Tessellator TESSELLATOR = RenderSystem.renderThreadTesselator();
    public static final BufferBuilder BUFFER = TESSELLATOR.getBuffer();
    //
    public static TTFFontRenderer tf = TTFFontRenderer.of("NotoSansJP-ExtraBold", 7);

    /**
     * When rendering using vanilla methods, you should call this method in order to ensure the GL state does not get
     * leaked. This means you need to manually set the required GL state during the callback.
     */
    public static void post(Runnable callback) {
        RenderBuffers.post(callback);
    }

    /**
     * @param matrices
     * @param p
     * @param color
     */
    public static void renderBox(MatrixStack matrices, BlockPos p, int color) {
        renderBox(matrices, new Box(p), color);
    }

    /**
     * Returns the TTFFontRenderer instance for rendering text.
     *
     * @return the TTFFontRenderer instance
     */
    public static TTFFontRenderer getFont() {
        return tf;
    }

    /**
     * @param matrices
     * @param box
     * @param color
     */
    public static void renderBox(MatrixStack matrices, Box box, int color) {
        if (!isFrustumVisible(box)) {
            return;
        }
        matrices.push();
        drawBox(matrices, box, color);
        matrices.pop();
    }

    /**
     * @param matrices
     * @param box
     */
    public static void drawBox(MatrixStack matrices, Box box, int color) {
        drawBox(matrices, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color);
    }

    /**
     * Draws a box spanning from [x1, y1, z1] to [x2, y2, z2].
     * The 3 axes centered at [x1, y1, z1] may be colored differently using
     * xAxisRed, yAxisGreen, and zAxisBlue.
     *
     * <p> Note the coordinates the box spans are relative to current
     * translation of the matrices.
     *
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawBox(MatrixStack matrices, double x1, double y1,
                               double z1, double x2, double y2, double z2, int color) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        QUADS.begin(matrix4f);
        QUADS.color(color);

        QUADS.vertex(x1, y1, z1).vertex(x2, y1, z1).vertex(x2, y1, z2).vertex(x1, y1, z2);
        QUADS.vertex(x1, y2, z1).vertex(x1, y2, z2).vertex(x2, y2, z2).vertex(x2, y2, z1);
        QUADS.vertex(x1, y1, z1).vertex(x1, y2, z1).vertex(x2, y2, z1).vertex(x2, y1, z1);
        QUADS.vertex(x2, y1, z1).vertex(x2, y2, z1).vertex(x2, y2, z2).vertex(x2, y1, z2);
        QUADS.vertex(x1, y1, z2).vertex(x2, y1, z2).vertex(x2, y2, z2).vertex(x1, y2, z2);
        QUADS.vertex(x1, y1, z1).vertex(x1, y1, z2).vertex(x1, y2, z2).vertex(x1, y2, z1);

        QUADS.end();
    }

    /**
     * @param p
     * @param width
     * @param color
     */
    public static void renderBoundingBox(MatrixStack matrices, BlockPos p,
                                         float width, int color) {
        renderBoundingBox(matrices, new Box(p), width, color);
    }

    /**
     * @param box
     * @param width
     * @param color
     */
    public static void renderBoundingBox(MatrixStack matrices, Box box,
                                         float width, int color) {
        if (!isFrustumVisible(box)) {
            return;
        }
        matrices.push();
        RenderSystem.lineWidth(width);
        drawBoundingBox(matrices, box, color);
        matrices.pop();
    }

    /**
     * @param matrices
     * @param box
     */
    public static void drawBoundingBox(MatrixStack matrices, Box box, int color) {
        drawBoundingBox(matrices, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color);
    }

    /**
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawBoundingBox(MatrixStack matrices, double x1, double y1,
                                       double z1, double x2, double y2, double z2, int color) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        LINES.begin(matrix4f);
        LINES.color(color);

        LINES.vertex(x1, y1, z1).vertex(x2, y1, z1);
        LINES.vertex(x2, y1, z1).vertex(x2, y1, z2);
        LINES.vertex(x2, y1, z2).vertex(x1, y1, z2);
        LINES.vertex(x1, y1, z2).vertex(x1, y1, z1);

        LINES.vertex(x1, y1, z1).vertex(x1, y2, z1);
        LINES.vertex(x2, y1, z1).vertex(x2, y2, z1);
        LINES.vertex(x2, y1, z2).vertex(x2, y2, z2);
        LINES.vertex(x1, y1, z2).vertex(x1, y2, z2);

        LINES.vertex(x1, y2, z1).vertex(x2, y2, z1);
        LINES.vertex(x2, y2, z1).vertex(x2, y2, z2);
        LINES.vertex(x2, y2, z2).vertex(x1, y2, z2);
        LINES.vertex(x1, y2, z2).vertex(x1, y2, z1);

        LINES.end();
    }

    /**
     * @param matrices
     * @param s
     * @param d
     * @param width
     */
    public static void renderLine(MatrixStack matrices, Vec3d s,
                                  Vec3d d, float width, int color) {
        renderLine(matrices, s.x, s.y, s.z, d.x, d.y, d.z, width, color);
    }

    /**
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param width
     */
    public static void renderLine(MatrixStack matrices, double x1, double y1,
                                  double z1, double x2, double y2, double z2,
                                  float width, int color) {
        matrices.push();
        RenderSystem.lineWidth(width);
        drawLine(matrices, x1, y1, z1, x2, y2, z2, color);
        matrices.pop();
    }

    /**
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawLine(MatrixStack matrices, double x1, double y1,
                                double z1, double x2, double y2, double z2, int color) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        LINES.begin(matrix4f);
        LINES.color(color);
        LINES.vertex(x1, y1, z1);
        LINES.vertex(x2, y2, z2);
        LINES.end();
    }

    /**
     * @param matrices
     * @param text
     * @param pos
     */
    public static void renderSign(MatrixStack matrices, String text, Vec3d pos) {
        renderSign(matrices, text, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * @param matrices
     * @param text
     * @param x1
     * @param x2
     * @param x3
     */
    public static void renderSign(MatrixStack matrices, String text,
                                  double x1, double x2, double x3) {
        double dist = Math.sqrt(mc.player.squaredDistanceTo(x1, x2, x3));
        float scaling = 0.0018f + Modules.NAMETAGS.getScaling() * (float) dist;
        if (dist <= 8.0) {
            scaling = 0.0245f;
        }
        Camera camera = mc.gameRenderer.getCamera();
        final Vec3d pos = camera.getPos();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrixStack.translate(x1 - pos.getX(), x2 - pos.getY(), x3 - pos.getZ());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStack.scale(-scaling, -scaling, -1.0f);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        VertexConsumerProvider.Immediate vertexConsumers =
                VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        float hwidth = mc.textRenderer.getWidth(text) / 2.0f;
        Fonts.VANILLA.drawWithShadow(matrixStack, text, -hwidth, 0.0f, -1);
        vertexConsumers.draw();
        RenderSystem.disableBlend();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        matrixStack.pop();
    }

    /**
     * @param box
     * @return
     */
    public static boolean isFrustumVisible(Box box) {
        return ((AccessorWorldRenderer) mc.worldRenderer).getFrustum().isVisible(box);
    }

    /**
     * @param matrices
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     */
    public static void rect(MatrixStack matrices, double x1, double y1,
                            double x2, double y2, int color) {
        rect(matrices, x1, y1, x2, y2, 0.0, color);
    }

    /**
     * @param matrices
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param z
     * @param color
     */
    public static void rect(MatrixStack matrices, double x1, double y1,
                            double x2, double y2, double z, int color) {
        x2 += x1;
        y2 += y1;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        double i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float f = (float) ColorHelper.Argb.getAlpha(color) / 255.0f;
        float g = (float) ColorHelper.Argb.getRed(color) / 255.0f;
        float h = (float) ColorHelper.Argb.getGreen(color) / 255.0f;
        float j = (float) ColorHelper.Argb.getBlue(color) / 255.0f;
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BUFFER.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_COLOR);
        BUFFER.vertex(matrix4f, (float) x1, (float) y1, (float) z)
                .color(g, h, j, f).next();
        BUFFER.vertex(matrix4f, (float) x1, (float) y2, (float) z)
                .color(g, h, j, f).next();
        BUFFER.vertex(matrix4f, (float) x2, (float) y2, (float) z)
                .color(g, h, j, f).next();
        BUFFER.vertex(matrix4f, (float) x2, (float) y1, (float) z)
                .color(g, h, j, f).next();
        BufferRenderer.drawWithGlobalProgram(BUFFER.end());
        RenderSystem.disableBlend();
    }

    public static void glowRoundRect(MatrixStack stack, float x1, float y1, float width1, float height1, float radius1, float glowWidth, Color color1, int opacity1, boolean left, boolean right, boolean bleft, boolean bright) {
        if (stack == null) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        stack.push();

        Matrix4f matrix = stack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_COLOR
        );

        float x2 = x1 + width1;
        float y2 = y1 + height1;

        float radius2 = radius1 + glowWidth;

        float x1outer = x1 - glowWidth;
        float y1outer = y1 - glowWidth;

        float x2outer = x2 + glowWidth;
        float y2outer = y2 + glowWidth;

        float xStart1;
        float yStart1;
        float xEnd1 = 0;
        float yEnd1 = 0;
        float xStart21;
        float yStart21;
        float xEnd21 = 0;
        float yEnd21 = 0;

        float xStart2;
        float yStart2;
        float xEnd2 = 0;
        float yEnd2 = 0;
        float xStart22;
        float yStart22;
        float xEnd22 = 0;
        float yEnd22 = 0;

        float xStart3;
        float yStart3;
        float xEnd3 = 0;
        float yEnd3 = 0;
        float xStart23;
        float yStart23;
        float xEnd23 = 0;
        float yEnd23 = 0;

        float xStart4;
        float yStart4;
        float xEnd4 = 0;
        float yEnd4 = 0;
        float xStart24;
        float yStart24;
        float xEnd24 = 0;
        float yEnd24 = 0;

        color1 = new Color(
                color1.getRed(),
                color1.getGreen(),
                color1.getBlue(),
                opacity1
        );
        Color color2 = new Color(
                color1.getRed(),
                color1.getGreen(),
                color1.getBlue(),
                0
        );

        for (int i = 0; i < 90; i += 3) {
            if (left) {
                bufferBuilder.vertex(matrix, x1, y1, 0).next();
                break;
            }

            xStart1 = MathUtil.getRoundedRectPoint(x1, radius1, i, 1);
            yStart1 = MathUtil.getRoundedRectPoint(y1, radius1, i, 2);
            xEnd1 = MathUtil.getRoundedRectPoint(x1, radius1, i + 3, 1);
            yEnd1 = MathUtil.getRoundedRectPoint(y1, radius1, i + 3, 2);

            xStart21 = MathUtil.getRoundedRectPoint(x1outer, radius2, i, 1);
            yStart21 = MathUtil.getRoundedRectPoint(y1outer, radius2, i, 2);
            xEnd21 = MathUtil.getRoundedRectPoint(x1outer, radius2, i + 3, 1);
            yEnd21 = MathUtil.getRoundedRectPoint(y1outer, radius2, i + 3, 2);

            bufferBuilder
                    .vertex(matrix, xStart1, yStart1, 0)
                    .color(color1.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xStart21, yStart21, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd21, yEnd21, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd1, yEnd1, 0)
                    .color(color1.getRGB())
                    .next();
        }

        for (int i = 90; i < 180; i += 3) {
            xStart2 = MathUtil.getRoundedRectPoint(x1, radius1, i, 3);
            yStart2 = MathUtil.getRoundedRectPoint(y2, radius1, i, 4);
            xEnd2 = MathUtil.getRoundedRectPoint(x1, radius1, i + 3, 3);
            yEnd2 = MathUtil.getRoundedRectPoint(y2, radius1, i + 3, 4);

            xStart22 = MathUtil.getRoundedRectPoint(x1outer, radius2, i, 3);
            yStart22 = MathUtil.getRoundedRectPoint(y2outer, radius2, i, 4);
            xEnd22 = MathUtil.getRoundedRectPoint(x1outer, radius2, i + 3, 3);
            yEnd22 = MathUtil.getRoundedRectPoint(y2outer, radius2, i + 3, 4);

            if (i == 90) {
                bufferBuilder
                        .vertex(matrix, xEnd1, yEnd1, 0)
                        .color(color1.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xEnd21, yEnd21, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart22, yStart22, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart2, yStart2, 0)
                        .color(color1.getRGB())
                        .next();
            }

            bufferBuilder
                    .vertex(matrix, xStart2, yStart2, 0)
                    .color(color1.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xStart22, yStart22, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd22, yEnd22, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd2, yEnd2, 0)
                    .color(color1.getRGB())
                    .next();
        }

        for (int i = 0; i < 90; i += 3) {
            xStart3 = MathUtil.getRoundedRectPoint(x2, radius1, i, 5);
            yStart3 = MathUtil.getRoundedRectPoint(y2, radius1, i, 6);
            xEnd3 = MathUtil.getRoundedRectPoint(x2, radius1, i + 3, 5);
            yEnd3 = MathUtil.getRoundedRectPoint(y2, radius1, i + 3, 6);

            xStart23 = MathUtil.getRoundedRectPoint(x2outer, radius2, i, 5);
            yStart23 = MathUtil.getRoundedRectPoint(y2outer, radius2, i, 6);
            xEnd23 = MathUtil.getRoundedRectPoint(x2outer, radius2, i + 3, 5);
            yEnd23 = MathUtil.getRoundedRectPoint(y2outer, radius2, i + 3, 6);

            if (i == 0) {
                bufferBuilder
                        .vertex(matrix, xEnd2, yEnd2, 0)
                        .color(color1.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xEnd22, yEnd22, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart23, yStart23, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart3, yStart3, 0)
                        .color(color1.getRGB())
                        .next();
            }

            bufferBuilder
                    .vertex(matrix, xStart3, yStart3, 0)
                    .color(color1.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xStart23, yStart23, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd23, yEnd23, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd3, yEnd3, 0)
                    .color(color1.getRGB())
                    .next();
        }

        for (int i = 90; i <= 177; i += 3) {
            xStart4 = MathUtil.getRoundedRectPoint(x2, radius1, i, 7);
            yStart4 = MathUtil.getRoundedRectPoint(y1, radius1, i, 8);
            xEnd4 = MathUtil.getRoundedRectPoint(x2, radius1, i + 3, 7);
            yEnd4 = MathUtil.getRoundedRectPoint(y1, radius1, i + 3, 8);

            xStart24 = MathUtil.getRoundedRectPoint(x2outer, radius2, i, 7);
            yStart24 = MathUtil.getRoundedRectPoint(y1outer, radius2, i, 8);
            xEnd24 = MathUtil.getRoundedRectPoint(x2outer, radius2, i + 3, 7);
            yEnd24 = MathUtil.getRoundedRectPoint(y1outer, radius2, i + 3, 8);

            if (i == 90) {
                bufferBuilder
                        .vertex(matrix, xEnd3, yEnd3, 0)
                        .color(color1.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xEnd23, yEnd23, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart24, yStart24, 0)
                        .color(color2.getRGB())
                        .next();
                bufferBuilder
                        .vertex(matrix, xStart4, yStart4, 0)
                        .color(color1.getRGB())
                        .next();
            }

            bufferBuilder
                    .vertex(matrix, xStart4, yStart4, 0)
                    .color(color1.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xStart24, yStart24, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd24, yEnd24, 0)
                    .color(color2.getRGB())
                    .next();
            bufferBuilder
                    .vertex(matrix, xEnd4, yEnd4, 0)
                    .color(color1.getRGB())
                    .next();
        }

        xStart1 = MathUtil.getRoundedRectPoint(x1, radius1, 0, 1);
        yStart1 = MathUtil.getRoundedRectPoint(y1, radius1, 0, 2);
        xStart21 = MathUtil.getRoundedRectPoint(x1outer, radius2, 0, 1);
        yStart21 = MathUtil.getRoundedRectPoint(y1outer, radius2, 0, 2);

        bufferBuilder
                .vertex(matrix, xEnd4, yEnd4, 0)
                .color(color1.getRGB())
                .next();
        bufferBuilder
                .vertex(matrix, xEnd24, yEnd24, 0)
                .color(color2.getRGB())
                .next();
        bufferBuilder
                .vertex(matrix, xStart21, yStart21, 0)
                .color(color2.getRGB())
                .next();
        bufferBuilder
                .vertex(matrix, xStart1, yStart1, 0)
                .color(color1.getRGB())
                .next();

        tessellator.draw();

        stack.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void drawTexture(DrawContext context, String imageName, float x, float y, float width, float height) {
        context.drawTexture(new Identifier("ovaqreborn", imageName), (int) x, (int) y, (int) width, (int) height, 0.0f, 0.0f, 16, 128, 16, 128);
    }

    public static void drawTexture(DrawContext context, Identifier image, int x, int y, int width, int height) {
        context.drawTexture(image, x, y, width, height, 0.0f, 0.0f, 16, 128, 16, 128);
    }

    public static void drawTexture(DrawContext context, String imageName, int x, int y, int width, int height) {
        context.drawTexture(new Identifier("ovaqreborn", imageName), x, y, width, height, 0.0f, 0.0f, 16, 128, 16, 128);
    }

    public static void drawTexture(DrawContext context, String imageName, int x, int y, int width, int height, int u, int v) {
        context.drawTexture(new Identifier("ovaqreborn", imageName), x, y, u, v, width, height);
    }

    public static void drawTexture(DrawContext context, String imageName, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        context.drawTexture(new Identifier("ovaqreborn", imageName), x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    /**
     * @param context
     * @param text
     * @param x
     * @param y
     * @param color
     */
    public static void renderText(DrawContext context, String text, float x, float y, int color) {
        tf.drawString(context.getMatrices(), text, (int) x, (int) y, color);
    }

    /**
     * @param text
     * @return
     */
    public static int textWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }
}
