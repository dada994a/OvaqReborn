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
    private int originalFov;

    public ZoomModule() {
        super("Zoom", "Allows you to zoom in", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        isZoomed = true;
        saveAndAdjustFOV();
    }

    @Override
    public void onDisable() {
        isZoomed = false;
        restoreOriginalFOV();
    }

    private void saveAndAdjustFOV() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;

        if (options != null) {
            SimpleOption<Integer> fovOption = options.getFov();
            originalFov = fovOption.getValue();
            fovOption.setValue((int) (originalFov / zoomLevelConfig.getValue()));
        }
    }

    private void restoreOriginalFOV() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;

        if (options != null) {
            SimpleOption<Integer> fovOption = options.getFov();
            fovOption.setValue(originalFov);
        }
    }

    @EventListener
    public void onTick(RunTickEvent event) {
    }
}
