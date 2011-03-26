package org.netmelody.cii.witness;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import org.netmelody.cii.domain.Build;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.Detective;

public final class DummyWitness implements Witness {

    private final Detective detective;
    private final Status[] statuses;

    public DummyWitness(Detective detective) {
        this.detective = detective;

        final EnumSet<Status> statusSet = EnumSet.of(Status.BROKEN, Status.GREEN);
        statuses = statusSet.toArray(new Status[statusSet.size()]);
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        final TargetGroup result = new TargetGroup(newArrayList(randomTarget(feature.name() + " - Smoke"),
                                            randomTarget(feature.name() + " - Integration"),
                                            randomTarget(feature.name() + " - Acceptance"),
                                            randomTarget(feature.name() + " - Release")));
        return result;
    }
    
    private Target randomTarget(String name) {
        final Random random = new Random();
        
        final Status status = statuses[random.nextInt(statuses.length)];
        final ArrayList<Build> builds = newArrayList();
        
        if (random.nextBoolean()) {
            builds.add(buildAt(percentageOf(random.nextInt(101))));
        }
        
        return new Target(name, name, status,
                builds,
                detective.sponsorsOf("dracula"));
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
}
