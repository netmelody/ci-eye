package org.netmelody.cieye.spies.teamcity;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Builds;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesMany;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesOne;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;
import org.netmelody.cieye.spies.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.TeamCityProjects;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class TeamCitySpy implements CiSpy {

    private final Contact contact;
    private final String endpoint;
    private final KnownOffendersDirectory detective;

    public TeamCitySpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.endpoint = endpoint;
        this.detective = detective;
        this.contact = network.makeContact(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        if (!endpoint.equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        contact.performBasicLogin(endpoint + "/guestAuth/");
        
        final Collection<Project> projects = filter(projects(), new Predicate<Project>() {
            @Override public boolean apply(Project project) {
                return project.name.trim().equals(feature.name().trim());
            }
        });
        if (projects.isEmpty()) {
            return new TargetGroup();
        }
        
        final Project project = projects.iterator().next();
        
        final Collection<Target> targets = transform(buildTypesFor(project), new Function<BuildType, Target>() {
            @Override public Target apply(BuildType buildType) {
                return targetFrom(buildType);
            }
        });
        
        return new TargetGroup(targets);
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
        
        if (!completedBuilds.build().isEmpty()) {
            final Build lastCompletedBuild = completedBuilds.build().iterator().next();
            if (Status.BROKEN.equals(lastCompletedBuild.status())) {
                contact.performBasicAuthentication("cieye", "cieye");
                contact.doPut(endpoint + lastCompletedBuild.href + "/comment", note);
            }
        }
        
        return true;
    }
    
    private Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project();
    }

    private Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType();
    }
    
    private Target targetFrom(BuildType buildType) {
        final BuildTypeDetail buildTypeDetail = makeTeamCityRestCall(endpoint + buildType.href, BuildTypeDetail.class);
        
        if (buildTypeDetail.paused) {
            return new Target(endpoint + buildType.href, buildType.webUrl, buildType.name, Status.DISABLED);
        }
        
        final List<Sponsor> sponsors = new ArrayList<Sponsor>();
        final List<org.netmelody.cieye.core.domain.RunningBuild> runningBuilds = new ArrayList<org.netmelody.cieye.core.domain.RunningBuild>();
        long startTime = 0L;
            
        for(Build build : runningBuildsFor(buildType)) {
            final BuildDetail buildDetail = detailsOf(build);
            startTime = Math.max(buildDetail.startDateTime(), startTime);
            sponsors.addAll(sponsorsOf(buildDetail));
            runningBuilds.add(new org.netmelody.cieye.core.domain.RunningBuild(percentageOf(build.percentageComplete), buildDetail.status()));
        }
        
        Status currentStatus = Status.GREEN;
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (!completedBuilds.build().isEmpty()) {
            final Build lastCompletedBuild = completedBuilds.build().iterator().next();
            currentStatus = lastCompletedBuild.status();
            if (runningBuilds.isEmpty() || Status.BROKEN.equals(currentStatus)) {
                final BuildDetail buildDetail = detailsOf(lastCompletedBuild);
                startTime = Math.max(buildDetail.startDateTime(), startTime);
                sponsors.addAll(sponsorsOf(buildDetail));
                currentStatus = buildDetail.status();
            }
        }
        
        return new Target(endpoint + buildType.href, buildType.webUrl, buildType.name, currentStatus, startTime, runningBuilds, sponsors);
    }

    private List<Build> runningBuildsFor(BuildType buildType) {
        return makeTeamCityRestCall(endpoint + "/app/rest/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class).build();
    }
    
    private BuildDetail detailsOf(Build build) {
        return makeTeamCityRestCall(endpoint + build.href, BuildDetail.class);
    }
    
    private Set<Sponsor> sponsorsOf(BuildDetail build) {
        return detective.search(analyseChanges(build));
    }

    private String analyseChanges(BuildDetail build) {
        if (build.changes == null || build.changes.count == 0) {
            return "";
        }
        
        final List<Change> changes = new ArrayList<Change>();
        if (build.changes.count == 1) {
            changes.add(makeTeamCityRestCall(endpoint + build.changes.href, ChangesOne.class).change);
        }
        else {
            changes.addAll(makeTeamCityRestCall(endpoint + build.changes.href, ChangesMany.class).change());
        }
        
        final StringBuilder result = new StringBuilder();
        for (Change change : changes) {
            final ChangeDetail changeDetail = makeTeamCityRestCall(endpoint + change.href, ChangeDetail.class);
            result.append(changeDetail.comment);
            result.append(changeDetail.username);
        }
        
        return result.toString();
    }

    private <T> T makeTeamCityRestCall(String url, Class<T> type) {
        return contact.makeJsonRestCall(url, type);
    }
}
