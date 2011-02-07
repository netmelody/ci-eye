package org.netmelody.cii.domain;

import static java.lang.Math.min;

public final class Percentage {

    private final int value;
    
    public Percentage() {
        this(0);
    }

    public Percentage(int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100 inclusive.");
        }
        this.value = value;
    }

    public static Percentage percentageOf(int value) {
        return new Percentage(value);
    }
    
    public static Percentage percentageOf(long numerator, long denominator) {
        return (denominator <= 0L) ? percentageOf(0) : percentageOf((int)min((((double)numerator) / denominator) * 100, 100));
    }
    
    public int value() {
        return value;
    }
}
