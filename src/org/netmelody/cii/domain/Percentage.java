package org.netmelody.cii.domain;

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
    
    public int value() {
        return value;
    }
}
