package org.netmelody.cieye.spies.teamcity;

import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.netmelody.cieye.core.domain.RunningBuild;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Investigation;

public final class BuildTypeAnalyser {

    private final TeamCityCommunicator communicator;
    private final KnownOffendersDirectory detective;

    public BuildTypeAnalyser(TeamCityCommunicator communicator, KnownOffendersDirectory detective) {
        this.communicator = communicator;
        this.detective = detective;
    }
    
    public TargetDetail targetFrom(BuildType buildType) {
        final BuildTypeDetail buildTypeDetail = communicator.detailsFor(buildType);
        
        if (buildTypeDetail.paused) {
            return new TargetDetail(communicator.endpoint() + buildType.href, buildType.webUrl(), buildType.name, Status.DISABLED, 0L);
        }
        
        final Set<Sponsor> sponsors = new HashSet<Sponsor>();
        final List<RunningBuild> runningBuilds = new ArrayList<RunningBuild>();
        long startTime = 0L;
            
        for(Build build : communicator.runningBuildsFor(buildType)) {
            final BuildDetail buildDetail = communicator.detailsOf(build);
            startTime = Math.max(buildDetail.startDateTime(), startTime);
            sponsors.addAll(sponsorsOf(buildDetail));
            runningBuilds.add(new RunningBuild(percentageOf(build.percentageComplete), buildDetail.status()));
        }
        
        Status currentStatus = Status.UNKNOWN;
        final Build lastCompletedBuild = communicator.lastCompletedBuildFor(buildTypeDetail);
        if (null != lastCompletedBuild) {
            currentStatus = lastCompletedBuild.status();
            if (runningBuilds.isEmpty() || Status.BROKEN.equals(currentStatus)) {
                final BuildDetail buildDetail = communicator.detailsOf(lastCompletedBuild);
                startTime = Math.max(buildDetail.startDateTime(), startTime);
                sponsors.addAll(sponsorsOf(buildDetail));
                currentStatus = buildDetail.status();
            }
        }
        
        if (Status.BROKEN.equals(currentStatus)) {
            final List<Investigation> investigations = communicator.investigationsOf(buildType);
            if (!investigations.isEmpty() && (investigations.get(0).startDateTime() > startTime) && investigations.get(0).underInvestigation()) {
                currentStatus = Status.UNDER_INVESTIGATION;
            }
        }
        
        return new TargetDetail(communicator.endpoint() + buildType.href, buildType.webUrl(), buildType.name, currentStatus, startTime, runningBuilds, sponsors);
    }

    private Set<Sponsor> sponsorsOf(BuildDetail build) {
        return detective.search(analyseChanges(build));
    }

    private String analyseChanges(BuildDetail build) {
        if (build.changes == null || build.changes.count == 0) {
            return "";
        }
        
        final List<Change> changes = communicator.changesOf(build);
        
        final StringBuilder result = new StringBuilder();
        for (Change change : changes) {
            final ChangeDetail changeDetail = communicator.detailedChangesOf(change);
            result.append(changeDetail.username);
            result.append(' ');
            result.append(changeDetail.comment);
            result.append(' ');
        }
        
        return result.toString();
    }
}
