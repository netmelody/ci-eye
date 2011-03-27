package org.netmelody.cii.witness;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cii.domain.Build.buildAt;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.netmelody.cii.domain.Build;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.Detective;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class DummyWitness implements Witness {

    private final Map<String, TargetGroup> groupMap;

    public DummyWitness(final Detective detective) {
        final Map<String, TargetGroupGenerator> generatorMap =
            new MapMaker().makeComputingMap(new Function<String, TargetGroupGenerator>() {
                @Override
                public TargetGroupGenerator apply(String featureName) {
                    return new TargetGroupGenerator(detective, featureName);
                }
            });
        
        groupMap = new MapMaker()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .makeComputingMap(new Function<String, TargetGroup>() {
                @Override
                public TargetGroup apply(String featureName) {
                    return generatorMap.get(featureName).updatedValue();
                }
            });
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        return groupMap.get(feature.name());
    }
    

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    private static final class TargetGroupGenerator {
        
        private final Detective detective;
        private final Status[] statuses;
        private TargetGroup group;
        
        private TargetGroupGenerator(Detective detective, String featureName) {
            this.detective = detective;

            statuses = EnumSet.of(Status.BROKEN, Status.GREEN)
                              .toArray(new Status[EnumSet.of(Status.BROKEN, Status.GREEN).size()]);
            
            group = new TargetGroup(newArrayList(randomTarget(featureName + " - Smoke"),
                                                 randomTarget(featureName + " - Integration"),
                                                 randomTarget(featureName + " - Acceptance"),
                                                 randomTarget(featureName + " - Release")));
        }
        
        private TargetGroup updatedValue() {
            final Random random = new Random();
            final List<Target> newTargets = newArrayList();
            for (Target target : group.targets()) {
                final List<Build> builds = target.builds();
                final List<Build> newBuilds = newArrayList();
                Status newStatus = target.status();
                
                if (builds.isEmpty()) {
                    if (random.nextInt(Status.GREEN.equals(target.status()) ? 30 : 10) == 0) {
                        newBuilds.add(buildAt(percentageOf(0), Status.GREEN));
                    }
                }
                else {
                    for (Build build : builds) {
                        Build newBuild = build.advancedBy(2);
                        final int progress = newBuild.progress().value();
                        if (progress >= 60 && progress <= 90 && random.nextInt(50) == 0) {
                            newBuild = newBuild.withStatus(Status.BROKEN);
                        }
                        if (progress == 100) {
                            newStatus = Status.BROKEN.equals(newBuild.status()) ? Status.BROKEN : Status.GREEN;
                        }
                        else {
                            newBuilds.add(newBuild);
                        }
                    }
                    if (random.nextInt(100) == 0) {
                        newBuilds.add(buildAt(percentageOf(0), Status.GREEN));
                    }
                }
                newTargets.add(target.withBuilds(newBuilds).withStatus(newStatus));
            }
            group = new TargetGroup(newTargets);
            return group;
        }

        private Target randomTarget(String name) {
            final Random random = new Random();
            
            final Status status = statuses[random.nextInt(statuses.length)];
            final ArrayList<Build> builds = newArrayList();
            
            if (random.nextBoolean()) {
                builds.add(buildAt(percentageOf(random.nextInt(101)), Status.GREEN));
            }
            
            return new Target(name, name, status,
                    builds,
                    detective.sponsorsOf("dracula"));
        }
    }
}
