package net.shoreline.client.impl.module.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.RunTickEvent;

import java.lang.reflect.Field;

public class ZoomModule extends ToggleModule {
    private final Config<Float> zoomLevelConfig = new NumberConfig<>("ZoomLevel", "Level of zoom", 2.0f, 1.0f, 5.0f); // 1x - 5x
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
            try {
                // FOV フィールドをリフレクションを使って取得
                Field fovField = GameOptions.class.getDeclaredField("fov");
                fovField.setAccessible(true); // アクセス可能に設定

                if (isZoomed) {
                    originalFov = (float) fovField.get(options); // 現在のFOVを保存
                    fovField.set(options, calculateZoomedFOV()); // ズームしたFOVを設定
                } else {
                    fovField.set(options, originalFov); // 元のFOVに戻す
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace(); // エラーを表示
            }
        }
    }

    private float calculateZoomedFOV() {
        return 70.0f / zoomLevelConfig.getValue(); // ズームレベルに基づいてズームする
    }

    @EventListener
    public void onTick(RunTickEvent event) {
        adjustFOV();
    }
}
