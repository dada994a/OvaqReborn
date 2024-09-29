package net.shoreline.client.impl.module.render;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.render.RenderManager;
import net.shoreline.client.impl.event.render.RenderWorldEvent;
import net.shoreline.client.impl.shaders.GradientProgram;
import net.shoreline.client.impl.shaders.CircleProgram;
import net.shoreline.client.impl.shaders.RoundedRectangleProgram;
import net.shoreline.client.impl.shaders.RectProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec2f;

import java.awt.*;

public class ShadersModule extends ToggleModule {

    private GradientProgram gradientProgram;
    private CircleProgram circleProgram;
    private RoundedRectangleProgram roundedRectangleProgram;
    private RectProgram rectProgram;

    // ユーザー設定用のConfig
    Config<Boolean> useGradient = new BooleanConfig("Use Gradient", "Enable Gradient Shader", true);
    Config<Boolean> useCircle = new BooleanConfig("Use Circle", "Enable Circle Shader", false);
    Config<Boolean> useRoundedRectangle = new BooleanConfig("Use Rounded Rectangle", "Enable Rounded Rectangle Shader", false);
    Config<Boolean> useRect = new BooleanConfig("Use Rectangle", "Enable Rectangle Shader", false);

    public ShadersModule() {
        super("Shaders", "Renders shaders in-game", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        gradientProgram = new GradientProgram();
        circleProgram = new CircleProgram();
        roundedRectangleProgram = new RoundedRectangleProgram();
        rectProgram = new RectProgram();

        MinecraftClient mc = MinecraftClient.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();

        // ユニフォームの設定
        if (useGradient.getValue()) {
            gradientProgram.setUniforms(new Vec2f(windowWidth, windowHeight));
            gradientProgram.use();
        }
        if (useCircle.getValue()) {
            circleProgram.use();
        }
        if (useRoundedRectangle.getValue()) {
            roundedRectangleProgram.setDimensions(100, 50);
            roundedRectangleProgram.setColor(Color.RED);
            roundedRectangleProgram.setRadius(10);
            roundedRectangleProgram.setSoftness(5);
            roundedRectangleProgram.use();
        }
        if (useRect.getValue()) {
            rectProgram.setUniforms(new Vec2f(50, 50), new float[]{1.0f, 0.0f, 0.0f, 1.0f}); // 例として赤色を設定
            rectProgram.use();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (gradientProgram != null) {
            gradientProgram.stopUse();
        }
        if (circleProgram != null) {
            circleProgram.stopUse();
        }
        if (roundedRectangleProgram != null) {
            roundedRectangleProgram.stopUse();
        }
        if (rectProgram != null) {
            rectProgram.stopUse();
        }
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        // 必要に応じて描画イベント処理を追加
    }
}
