package org.netmelody.cieye.core.domain;

import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

public final class Build {
    
    private final Percentage progress;
    private final Status status;

    public Build() {
        this(percentageOf(100), Status.GREEN);
    }

    public Build(Percentage progress, Status status) {
        this.progress = progress;
        this.status = status;
    }
    
    public static Build buildAt(Percentage progress) {
        return buildAt(progress, Status.UNKNOWN);
    }
    
    public static Build buildAt(Percentage progress, Status status) {
        return new Build(progress, status);
    }
    
    public Percentage progress() {
        return progress;
    }
    
    public Status status() {
        return status;
    }

    public Build advancedBy(int percentageIncrement) {
        return new Build(percentageOf(Math.min(progress.value() + percentageIncrement, 100)), status);
    }

    public Build withStatus(Status status) {
        return new Build(progress, status);
    }
}
