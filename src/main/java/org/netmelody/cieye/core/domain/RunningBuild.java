package org.netmelody.cieye.core.domain;

import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

public final class RunningBuild {
    
    private final Percentage progress;
    private final Status status;

    public RunningBuild() {
        this(percentageOf(100), Status.GREEN);
    }

    public RunningBuild(Percentage progress, Status status) {
        this.progress = progress;
        this.status = status;
    }
    
    public static RunningBuild buildAt(Percentage progress) {
        return buildAt(progress, Status.UNKNOWN);
    }
    
    public static RunningBuild buildAt(Percentage progress, Status status) {
        return new RunningBuild(progress, status);
    }
    
    public Percentage progress() {
        return progress;
    }
    
    public Status status() {
        return status;
    }

    public RunningBuild advancedBy(int percentageIncrement) {
        return new RunningBuild(percentageOf(Math.min(progress.value() + percentageIncrement, 100)), status);
    }

    public RunningBuild withStatus(Status status) {
        return new RunningBuild(progress, status);
    }
}
