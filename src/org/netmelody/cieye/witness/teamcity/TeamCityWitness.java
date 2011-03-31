package org.netmelody.cieye.witness.teamcity;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.netmelody.cieye.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netmelody.cieye.domain.Feature;
import org.netmelody.cieye.domain.Sponsor;
import org.netmelody.cieye.domain.Status;
import org.netmelody.cieye.domain.Target;
import org.netmelody.cieye.domain.TargetGroup;
import org.netmelody.cieye.persistence.Detective;
import org.netmelody.cieye.witness.Witness;
import org.netmelody.cieye.witness.protocol.JsonRestRequester;
import org.netmelody.cieye.witness.teamcity.jsondomain.Build;
import org.netmelody.cieye.witness.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.witness.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.witness.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.witness.teamcity.jsondomain.Builds;
import org.netmelody.cieye.witness.teamcity.jsondomain.Change;
import org.netmelody.cieye.witness.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cieye.witness.teamcity.jsondomain.ChangesMany;
import org.netmelody.cieye.witness.teamcity.jsondomain.ChangesOne;
import org.netmelody.cieye.witness.teamcity.jsondomain.Project;
import org.netmelody.cieye.witness.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cieye.witness.teamcity.jsondomain.TeamCityProjects;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.gson.GsonBuilder;

public final class TeamCityWitness implements Witness {

    private final JsonRestRequester restRequester =
        new JsonRestRequester(new GsonBuilder().setDateFormat("yyyyMMdd'T'HHmmssZ").create(),
                              new Function<String, String>() {
                                  @Override public String apply(String input) {  return input.replace("\"@", "\""); }
                              });
    
    private final String endpoint;
    private final Detective detective;

    public TeamCityWitness(String endpoint, Detective detective) {
        this.endpoint = endpoint;
        this.detective = detective;
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        restRequester.performBasicLogin(endpoint + "/guestAuth/");
        if (!endpoint.equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        Collection<Project> projects = filter(projects(), new Predicate<Project>() {
            @Override public boolean apply(Project project) {
                return project.name.startsWith(feature.name());
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
        return false;
    }
    
    private Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project;
    }

    private Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType;
    }
    
    private Target targetFrom(BuildType buildType) {
        final BuildTypeDetail buildTypeDetail = makeTeamCityRestCall(endpoint + buildType.href, BuildTypeDetail.class);
        
        if (buildTypeDetail.paused) {
            return new Target(endpoint + buildType.href, buildType.webUrl, buildType.name, Status.DISABLED);
        }
        
        final List<Sponsor> sponsors = new ArrayList<Sponsor>();
        final List<org.netmelody.cieye.domain.Build> runningBuilds = new ArrayList<org.netmelody.cieye.domain.Build>();
        long startTime = 0L;
            
        for(Build build : runningBuildsFor(buildType)) {
            final BuildDetail buildDetail = detailsOf(build);
            startTime = Math.max(buildDetail.startDateTime(), startTime);
            sponsors.addAll(sponsorsOf(buildDetail));
            runningBuilds.add(new org.netmelody.cieye.domain.Build(percentageOf(build.percentageComplete), buildDetail.status()));
        }
        
        Status currentStatus = Status.GREEN;
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (completedBuilds.build != null && !completedBuilds.build.isEmpty()) {
            final Build lastCompletedBuild = completedBuilds.build.iterator().next();
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
        final List<Build> result = makeTeamCityRestCall(endpoint + "/app/rest/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class).build;
        return (result == null) ? new ArrayList<Build>() : result;
    }
    
    private BuildDetail detailsOf(Build build) {
        return makeTeamCityRestCall(endpoint + build.href, BuildDetail.class);
    }
    
    private List<Sponsor> sponsorsOf(BuildDetail build) {
        return detective.sponsorsOf(analyseChanges(build));
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
            changes.addAll(makeTeamCityRestCall(endpoint + build.changes.href, ChangesMany.class).change);
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
        return restRequester.makeJsonRestCall(url, type);
    }
}
