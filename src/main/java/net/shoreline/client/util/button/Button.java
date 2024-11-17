package net.shoreline.client.util.button;

import net.minecraft.client.gui.DrawContext;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public interface Button {
    void render(DrawContext context, int mouseX, int mouseY, float delta);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}
