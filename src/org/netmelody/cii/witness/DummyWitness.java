package org.netmelody.cii.witness;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.Random;

import org.netmelody.cii.domain.Build;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;

public final class DummyWitness implements Witness {

    @Override
    public TargetGroup statusOf(Feature feature) {
        return new TargetGroup(newArrayList(randomTarget("Smoke"),
                                            randomTarget("Integration"),
                                            randomTarget("Acceptance"),
                                            randomTarget("Release")));
    }
    
    private Target randomTarget(String name) {
        final Random random = new Random();
        
        Build builds[] = new Build[0];
        if (random.nextBoolean()) {
            builds = new Build[]{ buildAt(percentageOf(random.nextInt(101))) };
        }
        return new Target(name, name, Status.values()[random.nextInt(3)], builds);
    }

    @Override
    public long millisecondsUntilNextUpdate() {
        return 0L;
    }
}
