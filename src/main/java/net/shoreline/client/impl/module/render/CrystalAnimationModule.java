package net.shoreline.client.impl.module.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.render.entity.RenderCrystalEvent;

public class CrystalAnimationModule extends ToggleModule {

    private final Config<Boolean> disableYAxis = new BooleanConfig("Disable Y Axis", "Removes Y axis movement in crystal animation", true);

    private float rotationAngle = 0.0f;

    public CrystalAnimationModule() {
        super("CrystalAnimation", "Customizes the animation of End Crystals", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderCrystal(RenderCrystalEvent event) {
        EndCrystalEntity crystal = event.endCrystalEntity;
        MatrixStack matrixStack = event.matrixStack;

        if (disableYAxis.getValue()) {
            // Disable Y-axis movement
            crystal.setPos(crystal.getX(), Math.round(crystal.getY()), crystal.getZ());
        }
    }
}
