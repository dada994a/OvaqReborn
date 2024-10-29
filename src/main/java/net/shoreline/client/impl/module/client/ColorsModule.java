package net.shoreline.client.impl.module.client;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.ColorConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ConcurrentModule;
import net.shoreline.client.api.module.ModuleCategory;

import java.awt.*;

/**
 * @author OvaqReborn
 * @since 1.0
 */
public class ColorsModule extends ConcurrentModule {
    Config<Color> colorConfig = new ColorConfig("Color", "The primary client color", new Color(0, 255, 255), false, false);
    Config<Boolean> rainbowConfig = new BooleanConfig("Rainbow", "Renders rainbow colors for modules", false);
    Config<Float> rainbowSpeedConfig = new NumberConfig<>("RainbowSpeed", "Controls the speed of rainbow color changes", 0.1f, 1.0f, 2.0f);

    /**
     *
     */
    public ColorsModule() {
        super("Colors", "Client color scheme", ModuleCategory.CLIENT);
    }

    public Color getColor() {
        if (rainbowConfig.getValue()) {
            return getRainbowColor(1.0f);
        }
        return colorConfig.getValue();
    }

    public Color getColor(float alpha) {
        if (rainbowConfig.getValue()) {
            return getRainbowColor(alpha);
        }
        ColorConfig config = (ColorConfig) colorConfig;
        return new Color(config.getRed() / 255.0f, config.getGreen() / 255.0f, config.getBlue() / 255.0f, alpha);
    }

    public Color getColor(int alpha) {
        if (rainbowConfig.getValue()) {
            return getRainbowColor(alpha / 255.0f);
        }
        ColorConfig config = (ColorConfig) colorConfig;
        return new Color(config.getRed(), config.getGreen(), config.getBlue(), alpha);
    }

    public Integer getRGB() {
        return getColor().getRGB();
    }

    public int getRGB(int a) {
        return getColor(a).getRGB();
    }

    /**
     * Generates a rainbow color based on the system time and speed setting.
     *
     * @param alpha The alpha (transparency) value for the color.
     * @return A Color object representing the rainbow color.
     */
    private Color getRainbowColor(float alpha) {
        float speed = rainbowSpeedConfig.getValue();
        float hue = (System.currentTimeMillis() % (int)(2000 / speed)) / (2000.0f / speed);
        Color color = Color.getHSBColor(hue, 0.8f, 0.8f);
        return new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
    }
}
