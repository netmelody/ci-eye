package org.netmelody.cieye.spies.demo;

import static com.google.common.cache.CacheLoader.from;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.netmelody.cieye.core.domain.Status.UNKNOWN;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netmelody.cieye.core.domain.*;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.demo.DemoModeFakeCiServer.BuildData;
import org.netmelody.cieye.spies.demo.DemoModeFakeCiServer.TargetData;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public final class DemoModeSpy implements CiSpy {

    private final LoadingCache<String, DemoModeFakeCiServer> demoCiServers =
        CacheBuilder.newBuilder().build(from(new Function<String, DemoModeFakeCiServer>() {
            @Override
            public DemoModeFakeCiServer apply(String featureName) {
                return new DemoModeFakeCiServer(featureName);
            }
        }));
    
    private final KnownOffendersDirectory detective;

    private static final class TargetInfo {
        public final String featureName;
        public final String targetName;
        public TargetInfo(String featureName, String targetName) {
            this.featureName = featureName;
            this.targetName = targetName;
        }
    }
    
    private final Map<TargetId, TargetInfo> recognisedTargets = newHashMap();
    
    public DemoModeSpy(final KnownOffendersDirectory detective) {
        this.detective = detective;
    }
    
    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        final String featureName = feature.name();
        final List<TargetDigest> digests = newArrayList();
        
        for (String targetName : demoCiServers.getUnchecked(featureName).getTargetNames()) {
            TargetDigest digest = new TargetDigest(featureName+targetName, "http://www.example.com/", targetName, UNKNOWN);
            digests.add(digest);
            recognisedTargets.put(digest.id(), new TargetInfo(featureName, targetName));
        }
        
        return new TargetDigestGroup(digests);
    }
    
    @Override
    public TargetDetail statusOf(final TargetId target, Flag showPersonalBuilds) {
        final TargetInfo targetInfo = recognisedTargets.get(target);
        if (null == targetInfo) {
            return null;
        }
        return detailsOf(targetInfo.featureName, targetInfo.targetName);
    }

    private TargetDetail detailsOf(String featureName, String targetName) {
        final DemoModeFakeCiServer ciServer = demoCiServers.getUnchecked(featureName);
        final TargetData data = ciServer.getDataFor(targetName);
        final List<RunningBuild> builds = new ArrayList<RunningBuild>();
        
        final StringBuilder commentry = new StringBuilder();
        for (BuildData buildData : data.builds) {
            builds.add(new RunningBuild(Percentage.percentageOf(buildData.progress),
                                 buildData.green ? Status.GREEN : data.note.isEmpty() ? Status.BROKEN : Status.UNDER_INVESTIGATION));
            commentry.append(buildData.checkinComments);
        }
        
        Status status = data.green ? Status.GREEN : (data.note.isEmpty() ? Status.BROKEN : Status.UNDER_INVESTIGATION);
        return new TargetDetail(featureName+targetName, data.url, targetName, status, 0L, builds, detective.search(commentry.toString()));
    }

    @Override
    public boolean takeNoteOf(TargetId target, String note) {
        if (!recognisedTargets.containsKey(target)) {
            return false;
        }
        
        final TargetInfo targetInfo = recognisedTargets.get(target);
        demoCiServers.getUnchecked(targetInfo.featureName).addNote(targetInfo.targetName, note);
        return true;
    }
}
