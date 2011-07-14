package org.netmelody.cieye.spies.teamcity;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.find;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Builds;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;
import org.netmelody.cieye.spies.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.TeamCityProjects;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class TeamCitySpy implements CiSpy {

    private final Contact contact;
    private final String endpoint;
    private final BuildTypeAnalyser buildTypeAnalyser;

    public TeamCitySpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.endpoint = endpoint;
        this.contact = network.makeContact(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
        this.buildTypeAnalyser = new BuildTypeAnalyser(this.contact, this.endpoint, detective);
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        if (!endpoint.equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        contact.performBasicLogin(endpoint + "/guestAuth/");
        
        final Project project = find(projects(), withName(feature.name()), null);
        
        if (null == project) {
            return new TargetGroup();
        }
        
        return new TargetGroup(transform(buildTypesFor(project), toTargets()));
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
        
        final BuildTypeDetail buildTypeDetail = makeTeamCityRestCall(targetId, BuildTypeDetail.class);
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        
        if (completedBuilds.build().isEmpty()) {
            return false;
        }

        final Build lastCompletedBuild = find(completedBuilds.build(), alwaysTrue());
        if (Status.BROKEN.equals(lastCompletedBuild.status())) {
            contact.performBasicAuthentication("cieye", "cieye");
            contact.doPut(endpoint + lastCompletedBuild.href + "/comment", note);
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
    
    private Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project();
    }

    private Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType();
    }
    
    private <T> T makeTeamCityRestCall(String url, Class<T> type) {
        return contact.makeJsonRestCall(url, type);
    }
}
