package org.netmelody.cii.domain;

import static org.netmelody.cii.domain.Percentage.percentageOf;

public final class Build {
    
    private final boolean building;
    private final Percentage progress;

    public Build() {
        this(false, percentageOf(100));
    }

    public Build(boolean building, Percentage progress) {
        this.building = building;
        this.progress = progress;
    }
    
    public static Build buildAt(Percentage progress) {
        return new Build(true, progress);
    }
    
    public boolean isBuilding() {
        return building;
    }
    
    public Percentage progress() {
        return progress;
    }
}
