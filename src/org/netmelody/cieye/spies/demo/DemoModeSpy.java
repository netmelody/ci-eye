package org.netmelody.cieye.spies.demo;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Percentage;
import org.netmelody.cieye.core.domain.RunningBuild;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.demo.DemoModeFakeCiServer.BuildData;
import org.netmelody.cieye.spies.demo.DemoModeFakeCiServer.TargetData;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public final class DemoModeSpy implements CiSpy {

    private final Map<String, DemoModeFakeCiServer> demoCiServers =
        new MapMaker().makeComputingMap(new Function<String, DemoModeFakeCiServer>() {
            @Override
            public DemoModeFakeCiServer apply(String featureName) {
                return new DemoModeFakeCiServer(featureName);
            }
        });
    
    private final KnownOffendersDirectory detective;

    public DemoModeSpy(final KnownOffendersDirectory detective) {
        this.detective = detective;
    }
    
    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        return new TargetDigestGroup();
    }
    
    @Override
    public TargetGroup statusOf(Feature feature) {
        final DemoModeFakeCiServer ciServer = demoCiServers.get(feature.name());
        TargetGroup result = new TargetGroup();
        
        final List<String> targetNames = ciServer.getTargetNames();
        for (String targetName : targetNames) {
            final TargetData data = ciServer.getDataFor(targetName);
            final List<RunningBuild> builds = new ArrayList<RunningBuild>();
            
            String commentry = "";
            for (BuildData buildData : data.builds) {
                builds.add(new RunningBuild(Percentage.percentageOf(buildData.progress),
                                     buildData.green ? Status.GREEN : data.note.isEmpty() ? Status.BROKEN : Status.UNDER_INVESTIGATION));
                commentry += buildData.checkinComments;
            }
            
            Status status = data.green ? Status.GREEN : (data.note.isEmpty() ? Status.BROKEN : Status.UNDER_INVESTIGATION);
            Target target = new Target(targetName, data.url, targetName, status, 0L, builds, detective.search(commentry));
            result = result.add(new TargetGroup(newArrayList(target)));
        }
        return result;
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        for (DemoModeFakeCiServer server : demoCiServers.values()) {
            server.addNote(targetId, note);
        }
        return true;
    }
}
