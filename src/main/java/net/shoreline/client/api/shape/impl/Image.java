package net.shoreline.client.api.shape.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.api.shape.Shape;
import net.shoreline.client.api.shape.ShapeProperties;

/**
 * @author OvaqReborn
 * @since 1.0
 */
@Setter
@Accessors(chain = true)
public class Image implements Shape {
    private MatrixStack matrixStack;
    private String texture;

    @Override
    public void render(ShapeProperties shapeProperties) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, new Identifier("ovaqreborn/" + texture));

        float width = shapeProperties.getWidth();
        float x = shapeProperties.getX() + width;
        float y = shapeProperties.getY();

        matrixStack.push();
        matrixStack.translate(x, y, 0.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
        matrixStack.translate(-x, -y, 0.0F);

        RenderManager.simpleQuad(
                matrixStack.peek().getPositionMatrix(),
                x,
                y,
                width,
                shapeProperties.getHeight(),
                shapeProperties.getColor().x);

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public void setMatrixStack(MatrixStack matrixStack) {
        this.matrixStack = matrixStack;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
}