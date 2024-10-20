package net.shoreline.client.api.render;

public class SliderSetting {

    private String name;
    private double value;
    private final double minValue;
    private final double maxValue;
    private final double step;

    public SliderSetting(String name, double value, double minValue, double maxValue, double step) {
        this.name = name;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (value < minValue) {
            this.value = minValue;
        } else if (value > maxValue) {
            this.value = maxValue;
        } else {
            this.value = value;
        }
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStep() {
        return step;
    }

    public void increase() {
        setValue(this.value + this.step);
    }

    public void decrease() {
        setValue(this.value - this.step);
    }
}
