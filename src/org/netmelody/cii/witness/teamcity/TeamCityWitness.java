package org.netmelody.cii.witness.teamcity;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.netmelody.cii.domain.Percentage.percentageOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.Sponsor;
import org.netmelody.cii.domain.Status;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.protocol.JsonRestRequester;
import org.netmelody.cii.witness.teamcity.jsondomain.Build;
import org.netmelody.cii.witness.teamcity.jsondomain.BuildDetail;
import org.netmelody.cii.witness.teamcity.jsondomain.BuildType;
import org.netmelody.cii.witness.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cii.witness.teamcity.jsondomain.Builds;
import org.netmelody.cii.witness.teamcity.jsondomain.Change;
import org.netmelody.cii.witness.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cii.witness.teamcity.jsondomain.ChangesMany;
import org.netmelody.cii.witness.teamcity.jsondomain.ChangesOne;
import org.netmelody.cii.witness.teamcity.jsondomain.Project;
import org.netmelody.cii.witness.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cii.witness.teamcity.jsondomain.TeamCityProjects;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.gson.GsonBuilder;

public final class TeamCityWitness implements Witness {

    private final JsonRestRequester restRequester =
        new JsonRestRequester(new GsonBuilder().create(),
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
        
    private Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project;
    }

    private Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType;
    }
    
    private Target targetFrom(BuildType buildType) {
        final BuildTypeDetail buildTypeDetail = makeTeamCityRestCall(endpoint + buildType.href, BuildTypeDetail.class);
        
        if (buildTypeDetail.paused) {
            return new Target(endpoint + buildType.href, buildType.name, Status.DISABLED);
        }
        
        final List<Sponsor> sponsors = new ArrayList<Sponsor>();
        final List<org.netmelody.cii.domain.Build> builds = new ArrayList<org.netmelody.cii.domain.Build>();
        
        final Builds runningBuilds = makeTeamCityRestCall(endpoint + "/app/rest/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class);
        if (runningBuilds.build != null && !runningBuilds.build.isEmpty()) {
            for(Build build : runningBuilds.build) {
                sponsors.addAll(sponsorsOf(build));
                builds.add(new org.netmelody.cii.domain.Build(percentageOf(build.percentageComplete), build.status()));
            }
        }
        
        Status currentStatus = Status.GREEN;
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (completedBuilds.build != null && !completedBuilds.build.isEmpty()) {
            final Build lastCompletedBuild = completedBuilds.build.iterator().next();
            if (builds.isEmpty()) {
                sponsors.addAll(sponsorsOf(lastCompletedBuild));
            }
            currentStatus = lastCompletedBuild.status();
        }
        
        return new Target(endpoint + buildType.href, buildType.name, currentStatus, builds, sponsors);
    }
    
    private List<Sponsor> sponsorsOf(Build build) {
        return detective.sponsorsOf(analyseChanges(makeTeamCityRestCall(endpoint + build.href, BuildDetail.class)));
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
