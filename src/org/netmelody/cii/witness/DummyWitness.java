package org.netmelody.cii.witness;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.Random;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;

public final class DummyWitness implements Witness {

    @Override
    public TargetGroup statusOf(Feature feature) {
        return new TargetGroup(newArrayList(randomTarget(feature.name() + " - Smoke"),
                                            randomTarget(feature.name() + " - Integration"),
                                            randomTarget(feature.name() + " - Acceptance"),
                                            randomTarget(feature.name() + " - Release")));
    }
    
    private Target randomTarget(String name) {
        final Random random = new Random();
        
        if (random.nextBoolean()) {
            return new Target(name, name, Status.values()[random.nextInt(Status.values().length)],
                              buildAt(percentageOf(random.nextInt(101))));
        }
        return new Target(name, name, Status.values()[random.nextInt(Status.values().length)]);
    }

    @Override
    public long millisecondsUntilNextUpdate() {
        return 0L;
    }
}
