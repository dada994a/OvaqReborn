package net.shoreline.client.api.config.setting;

import net.shoreline.client.api.config.Config;

public class SliderConfig<T extends Number> extends Config<T> {
    private final T minValue;
    private final T maxValue;
    private final double step;

    public SliderConfig(String name, T defaultValue, T minValue, T maxValue, double step) {
        super(name, String.valueOf(defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public double getStep() {
        return step;
    }

    @Override
    public void setValue(T value) {
        if (value.doubleValue() < minValue.doubleValue() || value.doubleValue() > maxValue.doubleValue()) {
            throw new IllegalArgumentException("Value out of range");
        }
        super.setValue(value);
    }

    public String getSuffix() {
        return "Value: " + getValue();
    }
}
