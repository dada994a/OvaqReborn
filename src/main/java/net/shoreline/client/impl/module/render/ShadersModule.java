package net.shoreline.client.impl.module.render;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.shaders.GradientProgram;
import net.shoreline.client.impl.shaders.RoundedRectangleProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;

import java.awt.*;

public class ShadersModule extends ToggleModule {

    private GradientProgram gradientProgram;
    private RoundedRectangleProgram roundedRectangleProgram;

    Config<Boolean> useGradient = new BooleanConfig("Use Gradient", "Enable Gradient Shader", true);
    Config<Boolean> useRoundedRectangle = new BooleanConfig("Use Rounded Rectangle", "Enable Rounded Rectangle Shader", false);

    public ShadersModule() {
        super("Shaders", "Renders shaders in-game", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        gradientProgram = new GradientProgram();
        roundedRectangleProgram = new RoundedRectangleProgram();

        MinecraftClient mc = MinecraftClient.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();

        // ユニフォームの設定
        if (useGradient.getValue()) {
            gradientProgram.setUniforms(new Vec2f(windowWidth, windowHeight));
            gradientProgram.use();
        }
        if (useRoundedRectangle.getValue()) {
            roundedRectangleProgram.setDimensions(100, 50);
            roundedRectangleProgram.setColor(Color.RED);
            roundedRectangleProgram.setRadius(10);
            roundedRectangleProgram.setSoftness(5);
            roundedRectangleProgram.use();
        }
        }
    }
