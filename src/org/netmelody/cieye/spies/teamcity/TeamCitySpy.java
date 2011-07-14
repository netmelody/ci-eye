package org.netmelody.cieye.spies.teamcity;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.find;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
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

    private final TeamCityRestRequester requester;
    private final String endpoint;
    private final BuildTypeAnalyser buildTypeAnalyser;

    public TeamCitySpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.endpoint = endpoint;
        this.requester = new TeamCityRestRequester(network, this.endpoint);
        this.buildTypeAnalyser = new BuildTypeAnalyser(this.requester, this.endpoint, detective);
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        if (!endpoint.equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        requester.loginAsGuest();
        
        final Project project = find(requester.projects(), withName(feature.name()), null);
        
        if (null == project) {
            return new TargetGroup();
        }
        
        return new TargetGroup(transform(requester.buildTypesFor(project), toTargets()));
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        if (!targetId.startsWith(endpoint)) {
            return false;
        }
        
        final BuildTypeDetail buildTypeDetail = requester.detailsFor(targetId);
        final Build lastCompletedBuild = requester.lastCompletedBuildFor(buildTypeDetail);
        
        if (null == lastCompletedBuild) {
            return false;
        }
        
        if (Status.BROKEN.equals(lastCompletedBuild.status())) {
            requester.commentOn(lastCompletedBuild, note);
            return true;
        }

        return false;
    }
    
    private Function<BuildType, Target> toTargets() {
        return new Function<BuildType, Target>() {
            @Override public Target apply(BuildType buildType) {
                return buildTypeAnalyser.targetFrom(buildType);
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
