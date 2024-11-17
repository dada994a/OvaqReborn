package net.shoreline.client.impl.font;

import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.util.AlphaOverride;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class TTFFontRenderer {
    public static TTFFontRenderer of(String asset, int size) {
        String assetDir = "assets/ovaqreborn/fonts/" + asset + ".ttf";
        try {
            return new TTFFontRenderer(
                    Font.createFont(
                            Font.TRUETYPE_FONT,
                            Objects.requireNonNull(TTFFontRenderer.class.getClassLoader().getResourceAsStream(assetDir))
                    ).deriveFont(Font.PLAIN, size),
                    size
            );
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private final FontRenderer fontRenderer;

    final float si;

    private TTFFontRenderer(Font font, float si) {
        this.fontRenderer = new FontRenderer(new Font[] { font }, si);
        this.si = si;
    }

    public float getSize() {
        return si;
    }

    public void drawStringShadow(MatrixStack matrices, String text, float x, float y, int color) {
        drawString(matrices, text, x + 0.5f, y + 0.5f, 0xff000000);
        drawString(matrices, text, x, y, color);
    }

    public void drawString(MatrixStack matrices, String text, float x, float y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) {
            color1 |= 0xff000000;
        }
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawString(matrices, text, x, y, r, g, b, a);
    }

    public void drawString(MatrixStack matrices, String text, double x, double y, int color) {
        drawString(matrices, text, (float) x, (float) y, color);
    }

    public void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a) {
        float v = AlphaOverride.compute((int) (a * 255)) / 255;
        fontRenderer.drawString(matrices, text, x, y, r, g, b, v);
    }

    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) {
            color1 |= 0xff000000;
        }
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawCenteredString(matrices, text, x, y, r, g, b, a);
    }

    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a) {
        float v = AlphaOverride.compute((int) (a * 255)) / 255;
        fontRenderer.drawCenteredString(matrices, text, (float) x, (float) y, r, g, b, v);
    }

    public float getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    public float getFontHeight() {
        return fontRenderer.getStringHeight("abcdefg123");
    }

    public float getFontHeight(String text) {
        return getFontHeight();
    }

    public float getMarginHeight() {
        return getFontHeight();
    }

    public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        drawString(matrices, s, x, y, color);
    }

    public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow) {
        drawString(matrices, s, x, y, r, g, b, a);
    }

    public String trimStringToWidth(String in, double width) {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (getStringWidth(sb.toString() + c) >= width) {
                return sb.toString();
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public String trimStringToWidth(String in, double width, boolean reverse) {
        return trimStringToWidth(in, width);
    }
}
