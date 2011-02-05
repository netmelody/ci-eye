package org.netmelody.cii.witness;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;

public final class DummyWitness implements Witness {

    @Override
    public TargetGroup targetList() {
        return new TargetGroup(newArrayList(new Target("Smoke", Status.GREEN),
                                            new Target("Integration", Status.BROKEN),
                                            new Target("Legacy", Status.DISABLED),
                                            new Target("Active", Status.GREEN, buildAt(percentageOf(12)))));
    }

}
