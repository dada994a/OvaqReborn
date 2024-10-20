package net.shoreline.client.api.config.setting;

import net.shoreline.client.api.config.Config;

public class SliderConfig<T extends Number> extends Config<T> {
    private final T minValue;  // スライダーの最小値
    private final T maxValue;  // スライダーの最大値
    private final double step;  // スライダーのステップ

    public SliderConfig(String name, T defaultValue, T minValue, T maxValue, double step) {
        super(name, String.valueOf(defaultValue));  // スーパークラスのコンストラクタを呼び出し
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
        // 値が範囲内に収まっているか確認
        if (value.doubleValue() < minValue.doubleValue() || value.doubleValue() > maxValue.doubleValue()) {
            throw new IllegalArgumentException("Value out of range");
        }
        super.setValue(value);
    }

    // スライダーの値を表示するためのメソッド
    public String getSuffix() {
        return "Value: " + getValue();  // 値の表示形式をカスタマイズ
    }
}
