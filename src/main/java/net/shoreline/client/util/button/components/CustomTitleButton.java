package net.shoreline.client.util.button.components;

import net.minecraft.client.gui.DrawContext;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.util.button.AbstractButton;
import net.shoreline.client.util.math.MathUtil;

import java.awt.*;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class CustomTitleButton extends AbstractButton {

    public CustomTitleButton(String name, Runnable action) {
        super(name, action);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = MathUtil.isHovered(mouseX, mouseY, x, y, width, height)
                ? 0xFF232431
                : 0xFF191a28;

        RenderManager.rect(context.getMatrices(), x, y, width, height, color);

        int glowColor = MathUtil.isHovered(mouseX, mouseY, x, y, width, height)
                ? 0xFF5555FF
                : 0xFF3333FF;
        RenderManager.glowRoundRect(context.getMatrices(), x, y, width, height, 5, 5, new Color(glowColor), 100, false, false, false, false);
        RenderManager.tf.drawCenteredString(context.getMatrices(), name, x + width / 2 - 1,
                y + height / 2 - 2, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            action.run();
        }
        return false;
    }
}
