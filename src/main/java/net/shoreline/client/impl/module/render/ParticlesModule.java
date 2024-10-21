package net.shoreline.client.impl.module.render;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.particle.ParticleEvent;
import net.shoreline.client.impl.event.particle.TotemParticleEvent;

import java.awt.*;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class ParticlesModule extends ToggleModule {

    Config<TotemParticle> totemConfig = new EnumConfig<>("Totem", "Renders totem particles", TotemParticle.OFF, TotemParticle.values());
    Config<Color> totemColorConfig1 = new ColorConfig("TotemColor1", "First color of the totem particles", new Color(25, 120, 0), false, false, () -> totemConfig.getValue() == TotemParticle.COLOR);
    Config<Color> totemColorConfig2 = new ColorConfig("TotemColor2", "Second color of the totem particles", new Color(255, 0, 0), false, false, () -> totemConfig.getValue() == TotemParticle.COLOR);
    Config<Boolean> fireworkConfig = new BooleanConfig("Firework", "Renders firework particles", false);
    Config<Boolean> potionConfig = new BooleanConfig("Effects", "Renders potion effect particles", true);
    Config<Boolean> bottleConfig = new BooleanConfig("BottleSplash", "Render bottle splash particles", true);
    Config<Boolean> portalConfig = new BooleanConfig("Portal", "Render portal particles", true);

    public ParticlesModule() {
        super("Particles", "Change the rendering of particles", ModuleCategory.RENDER);
    }

    @EventListener
    public void onParticle(ParticleEvent event) {
        if (potionConfig.getValue() && event.getParticleType() == ParticleTypes.ENTITY_EFFECT
                || fireworkConfig.getValue() && event.getParticleType() == ParticleTypes.FIREWORK
                || bottleConfig.getValue() && (event.getParticleType() == ParticleTypes.EFFECT || event.getParticleType() == ParticleTypes.INSTANT_EFFECT)
                || portalConfig.getValue() && event.getParticleType() == ParticleTypes.PORTAL) {
            event.cancel();
        }
    }

    @EventListener
    public void onTotemParticle(TotemParticleEvent event) {
        if (totemConfig.getValue() == TotemParticle.COLOR) {
            event.cancel();
            // Get the two colors for fading
            Color color1 = totemColorConfig1.getValue();
            Color color2 = totemColorConfig2.getValue();
            // Generate a random color between the two colors
            event.setColor(generateRandomColor(color1, color2));
        }
    }

    private Color generateRandomColor(Color color1, Color color2) {
        // Generate a random ratio between 0 and 1
        double ratio = Math.random(); // Using Math.random() for clarity
        int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    @EventListener
    public void onParticleEmitter(ParticleEvent.Emitter event) {
        if (totemConfig.getValue() == TotemParticle.REMOVE && event.getParticleType() == ParticleTypes.TOTEM_OF_UNDYING) {
            event.cancel();
        }
    }

    private enum TotemParticle {
        OFF,
        REMOVE,
        COLOR
    }
}
