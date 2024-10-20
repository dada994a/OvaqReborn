package net.shoreline.client.impl.module.graphics;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import org.jetbrains.annotations.Debug;

public class ShadersModule extends ToggleModule {
    public enum ShaderType {
        SOLID, RAINBOW, GRADIENT
    }

    public Config<ShaderType> shaderTypeConfig = new EnumConfig<>("Shader", "Select shader type", ShaderType.SOLID, ShaderType.values());
    public Config<Float> rangeConfig = new NumberConfig<>("Range", "Shader range", 10.0f, 1.0f, 64.0f);
    public Config<Float> lineWidthConfig = new NumberConfig<>("LineWidth", "Width of the lines", 1.0f, 0.1f, 5.0f);
    public Config<Float> glowConfig = new NumberConfig<>("Glow", "Glow intensity", 1.5f, 0.0f, 10.0f);

    public ShadersModule() {
        super("Shaders", "Apply solid shader effects", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRender() {
        switch (shaderTypeConfig.getValue()) {
            case SOLID:
                applySolidShader();
                break;
            case RAINBOW:
                applyRainbowShader();
                break;
            case GRADIENT:
                applyGradientShader();
                break;
        }
    }

    private void applySolidShader() {

    }

    private void applyRainbowShader() {
    }

    private void applyGradientShader() {
    }
}
