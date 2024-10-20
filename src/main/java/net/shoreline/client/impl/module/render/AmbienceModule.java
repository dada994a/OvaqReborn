package net.shoreline.client.impl.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.Render3DEvent;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.network.PacketEvent;

import java.awt.*;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class AmbienceModule extends ToggleModule {

    private final ColorConfig worldColorConfig = new ColorConfig("World Color", "Custom world color", Color.WHITE);
    private final ColorConfig fogColorConfig = new ColorConfig("Fog Color", "Custom fog color", new Color(0xCC7DD5));
    private final ColorConfig skyColorConfig = new ColorConfig("Sky Color", "Custom sky color", Color.BLACK);
    private final BooleanConfig customTimeConfig = new BooleanConfig("Custom Time", "Enable custom time", false);
    private final NumberConfig<Integer> timeConfig = new NumberConfig<>("Time", "Time of day", 0, 0, 24000, () -> customTimeConfig.getValue());
    private final BooleanConfig fogDistanceConfig = new BooleanConfig("Fog Distance", "Enable custom fog distance", false);
    private final NumberConfig<Integer> fogStartConfig = new NumberConfig<>("Fog Start", "Fog start distance", 50, 0, 1000, () -> fogDistanceConfig.getValue());
    private final NumberConfig<Integer> fogEndConfig = new NumberConfig<>("Fog End", "Fog end distance", 100, 0, 1000, () -> fogDistanceConfig.getValue());

    private long oldTime;

    public AmbienceModule() {
        super("Ambience", "Custom ambient settings for the world", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.world == null) return;
        oldTime = mc.world.getTimeOfDay();
    }

    @Override
    public void onDisable() {
        if (mc.world != null) {
            mc.world.setTimeOfDay(oldTime);
        }
    }

    @EventListener
    public void onTick() {
        if (mc.world != null && customTimeConfig.getValue()) {
            mc.world.setTimeOfDay(timeConfig.getValue().longValue());
        }
    }

    @EventListener
    public void onReceivePacket(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            oldTime = ((WorldTimeUpdateS2CPacket) event.getPacket()).getTime();
            event.cancel();
        }
    }

    @EventListener
    public void onRender(Render3DEvent event) {
        if (fogDistanceConfig.getValue()) {
            applyFogSettings(event.getMatrixStack());
        }
        applyColorSettings();
    }

    private void applyFogSettings(MatrixStack matrixStack) {

        int fogStart = getFogStart();
        int fogEnd = getFogEnd();

    }

    private void applyColorSettings() {
        Color worldColor = getWorldColor();
        Color skyColor = getSkyColor();
        Color fogColor = getFogColor();
         RenderSystem.clearColor(worldColor.getRed() / 255f, worldColor.getGreen() / 255f, worldColor.getBlue() / 255f, 1.0f);
    }
    public Color getWorldColor() {
        return worldColorConfig.getValue();
    }

    public Color getFogColor() {
        return fogColorConfig.getValue();
    }

    public Color getSkyColor() {
        return skyColorConfig.getValue();
    }

    public int getFogStart() {
        return MathHelper.clamp(fogStartConfig.getValue(), 0, 1000);
    }

    public int getFogEnd() {
        return MathHelper.clamp(fogEndConfig.getValue(), 0, 1000);
    }

    public boolean isFogDistanceEnabled() {
        return fogDistanceConfig.getValue();
    }
}
