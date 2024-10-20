package net.shoreline.client.api.render;

import java.awt.Color;

public class ColorSetting {

    private String name;
    private Color value;

    public ColorSetting(String name, Color value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Color getValue() {
        return value;
    }

    public void setValue(Color value) {
        this.value = value;
    }

    public int getAlpha() {
        return value.getAlpha();
    }

    public int getRed() {
        return value.getRed();
    }

    public int getGreen() {
        return value.getGreen();
    }

    public int getBlue() {
        return value.getBlue();
    }
}
