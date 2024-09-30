package net.shoreline.client.impl.module.render;

import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;

public class ShadersModule extends ToggleModule {
    public ShadersModule() {
        super("Shaders", "Renders shaders in-game", ModuleCategory.RENDER);
    }
}