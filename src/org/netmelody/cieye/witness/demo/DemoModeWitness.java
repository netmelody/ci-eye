package org.netmelody.cieye.witness.demo;

import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cieye.core.domain.Build.buildAt;
import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.netmelody.cieye.core.domain.Build;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.Detective;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class DemoModeWitness implements CiSpy {

    private final Map<String, TargetGroupGenerator> generatorMap;
    private final Map<String, TargetGroup> groupMap;

    public DemoModeWitness(final Detective detective) {
        generatorMap =
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
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        final Set<Entry<String, TargetGroup>> entries = groupMap.entrySet();
        for (Entry<String, TargetGroup> entry : entries) {
            final TargetGroup group = entry.getValue();
            final List<Target> targets = group.targets();
            for (Target target : targets) {
                if (targetId.equals(target.id())) {
                    generatorMap.get(entry.getKey()).noteReceivedFor(target.id());
                    return true;
                }
            }
        }
        return false;
    }
    
    private static final class TargetGroupGenerator {
        
        private final Set<String> notes = new HashSet<String>();
        
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
        
        public void noteReceivedFor(String id) {
            notes.add(id);
        }

        private TargetGroup updatedValue() {
            final Random random = new Random();
            final List<Target> newTargets = newArrayList();
            for (Target target : group.targets()) {
                final List<Build> builds = target.builds();
                final List<Build> newBuilds = newArrayList();
                Status newStatus = target.status();
                
                if (notes.remove(target.id()) && !Status.GREEN.equals(target.status())) {
                    newStatus = Status.UNDER_INVESTIGATION;
                }
                
                if (builds.isEmpty()) {
                    if (random.nextInt(Status.GREEN.equals(target.status()) ? 30 : 10) == 0) {
                        newBuilds.add(buildAt(percentageOf(0), Status.GREEN));
                    }
                }
                else {
                    int minProgress = 100;
                    for (Build build : builds) {
                        Build newBuild = build.advancedBy(2);
                        final int progress = newBuild.progress().value();
                        minProgress = Math.min(minProgress, progress);
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
                    if (minProgress > 50 && random.nextInt(100) == 0) {
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
            
            return new Target(name, "http://www.example.com/", name, status, 0L, builds, detective.sponsorsOf("dracula"));
        }
    }
}
