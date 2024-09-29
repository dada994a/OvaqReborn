package net.shoreline.client.impl.module.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.RunTickEvent;

public class ZoomModule extends ToggleModule {
    private final Config<Float> zoomLevelConfig = new NumberConfig<>("ZoomLevel", "Level of zoom", 2.0f, 1.0f, 5.0f);
    private boolean isZoomed = false;
    private float originalFov;

    public ZoomModule() {
        super("Zoom", "Allows you to zoom in", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        isZoomed = true;
        adjustFOV();
    }

    @Override
    public void onDisable() {
        isZoomed = false;
        adjustFOV();
    }

    private void adjustFOV() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;

        if (options != null) {
            SimpleOption<Float> fovOption = (SimpleOption<Float>) options.fov; // FOVオプションを取得

            if (isZoomed) {
                originalFov = fovOption.getValue(); // 現在のFOVを保存
                fovOption.setValue(calculateZoomedFOV()); // ズームしたFOVを設定
            } else {
                fovOption.setValue(originalFov); // 元のFOVに戻す
            }
        }
    }

    private float calculateZoomedFOV() {
        return 70.0f / zoomLevelConfig.getValue(); // ズームレベルに基づいてズームする
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        if (isZoomed) {
            adjustFOV(); // ズーム中のみFOVを調整
        }
    }
}
