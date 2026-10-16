package top.fpsmaster.features.settings.impl;

import top.fpsmaster.features.settings.Setting;

public class NumberSetting extends Setting<Number> {

    public Number min;
    public Number max;
    public Number inc;

    public NumberSetting(String name, Number value, Number min, Number max, Number inc) {
        super(name, value);
        this.min = min;
        this.max = max;
        this.inc = inc;
    }

    public NumberSetting(String name, Number value, Number min, Number max, Number inc, VisibleCondition visible) {
        super(name, value, visible);
        this.min = min;
        this.max = max;
        this.inc = inc;
    }

    @Override
    public void setValue(Number newValue) {
        double closestMultipleOfInc = Math.round(newValue.doubleValue() / inc.doubleValue()) * inc.doubleValue();
        closestMultipleOfInc = Math.round(closestMultipleOfInc * 100) / 100.0;
        double clamped = Math.max(min.doubleValue(), Math.min(max.doubleValue(), closestMultipleOfInc));
        // The stored value may be an Integer default while the new one is a Double: compare numerically.
        if (getValue() != null && getValue().doubleValue() == clamped) {
            return;
        }
        super.setValue(clamped);
    }
}



