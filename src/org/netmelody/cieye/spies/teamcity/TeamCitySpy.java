package org.netmelody.cieye.spies.teamcity;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;
import static org.netmelody.cieye.core.domain.Status.UNKNOWN;

import java.util.Collection;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class TeamCitySpy implements CiSpy {

    private final TeamCityCommunicator communicator;
    private final BuildTypeAnalyser buildTypeAnalyser;

    public TeamCitySpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.communicator = new TeamCityCommunicator(network, endpoint);
        this.buildTypeAnalyser = new BuildTypeAnalyser(this.communicator, detective);
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        return new TargetDigestGroup(transform(buildTypesFor(feature), toTargetDigests()));
    }
    
    @Override
    public TargetGroup statusOf(final Feature feature) {
        return new TargetGroup(transform(buildTypesFor(feature), toTargets()));
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        if (!targetId.startsWith(communicator.endpoint())) {
            return false;
        }
        
        final BuildTypeDetail buildTypeDetail = communicator.detailsFor(targetId);
        final Build lastCompletedBuild = communicator.lastCompletedBuildFor(buildTypeDetail);
        if (null != lastCompletedBuild && Status.BROKEN.equals(lastCompletedBuild.status())) {
            communicator.commentOn(lastCompletedBuild, note);
        }

        return true;
    }
    
    private Collection<BuildType> buildTypesFor(final Feature feature) {
        if (!communicator.canSpeakFor(feature)) {
            return newArrayList();
        }
        
        communicator.loginAsGuest();
        
        final Project project = find(communicator.projects(), withName(feature.name()), null);
        if (null == project) {
            return newArrayList();
        }
        
        return communicator.buildTypesFor(project);
    }

    private Function<BuildType, Target> toTargets() {
        return new Function<BuildType, Target>() {
            @Override public Target apply(BuildType buildType) {
                return buildTypeAnalyser.targetFrom(buildType);
            }
        };
    }

    private Function<BuildType, TargetDigest> toTargetDigests() {
        return new Function<BuildType, TargetDigest>() {
            @Override public TargetDigest apply(BuildType buildType) {
                return new TargetDigest(communicator.endpoint() + buildType.href, buildType.webUrl, buildType.name, UNKNOWN);
            }
        };
    }
    
    private Predicate<Project> withName(final String featureName) {
        return new Predicate<Project>() {
            @Override public boolean apply(Project project) {
                return project.name.trim().equals(featureName.trim());
            }
        };
    }
}
