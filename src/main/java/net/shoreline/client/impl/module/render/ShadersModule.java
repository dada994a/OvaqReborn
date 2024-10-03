package net.shoreline.client.impl.module.render;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.shaders.GradientProgram;
import net.shoreline.client.impl.shaders.RoundedRectangleProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;
import java.awt.*;

public class ShadersModule extends ToggleModule {

    public ShadersModule() {
        super("Shaders", "Renders shaders in-game", ModuleCategory.RENDER);
    }
}
