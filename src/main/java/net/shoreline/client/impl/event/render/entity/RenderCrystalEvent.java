package net.shoreline.client.impl.event.render.entity;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

/**
 * Event triggered during the rendering of End Crystals.
 */
@Cancelable
public class RenderCrystalEvent extends Event {
    public final EndCrystalEntity endCrystalEntity;
    public final float f;
    public final float g;
    public final MatrixStack matrixStack;
    public final int i;
    public final ModelPart core;
    public final ModelPart frame;

    /**
     * Constructor for RenderCrystalEvent.
     *
     * @param endCrystalEntity The End Crystal entity
     * @param f               The first parameter of the render method
     * @param g               The second parameter of the render method
     * @param matrixStack     The matrix stack used for rendering
     * @param i               The integer parameter used for rendering
     * @param core            The model part representing the core of the crystal
     * @param frame           The model part representing the frame of the crystal
     */
    public RenderCrystalEvent(EndCrystalEntity endCrystalEntity, float f, float g,
                              MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
        this.endCrystalEntity = endCrystalEntity;
        this.f = f;
        this.g = g;
        this.matrixStack = matrixStack;
        this.i = i;
        this.core = core;
        this.frame = frame;
    }
}
