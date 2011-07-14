package org.netmelody.cieye.spies.teamcity;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Iterables.find;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
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

public final class TeamCityRestRequester {

    private final Contact contact;
    private final String endpoint;

    public TeamCityRestRequester(CommunicationNetwork network, String endpoint) {
        this.contact = network.makeContact(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
        this.endpoint = endpoint;
    }
    
    public void loginAsGuest() {
        contact.performBasicLogin(endpoint + "/guestAuth/");
    }
    
    public Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project();
    }

    public Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType();
    }
    
    public BuildTypeDetail detailsFor(BuildType buildType) {
        return detailsFor(endpoint + buildType.href);
    }
    
    public BuildTypeDetail detailsFor(String href) {
        return makeTeamCityRestCall(href, BuildTypeDetail.class);
    }
    
    public Build lastCompletedBuildFor(BuildTypeDetail buildTypeDetail) {
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (completedBuilds.build().isEmpty()) {
            return null;
        }
        return find(completedBuilds.build(), alwaysTrue());
    }
    
    public List<Build> runningBuildsFor(BuildType buildType) {
        return makeTeamCityRestCall(endpoint + "/app/rest/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class).build();
    }
    
    public BuildDetail detailsOf(Build build) {
        return makeTeamCityRestCall(endpoint + build.href, BuildDetail.class);
    }
    
    public void commentOn(Build lastCompletedBuild, String note) {
        contact.performBasicAuthentication("cieye", "cieye");
        contact.doPut(endpoint + lastCompletedBuild.href + "/comment", note);
    }
    
    public List<Change> changesOf(BuildDetail buildDetail) {
        final List<Change> changes = new ArrayList<Change>();
        if (buildDetail.changes.count == 1) {
            changes.add(makeTeamCityRestCall(endpoint + buildDetail.changes.href, ChangesOne.class).change);
        }
        else {
            changes.addAll(makeTeamCityRestCall(endpoint + buildDetail.changes.href, ChangesMany.class).change());
        }
        return changes;
    }
    
    public ChangeDetail detailedChangesOf(Change change) {
        return makeTeamCityRestCall(endpoint + change.href, ChangeDetail.class);
    }
    
    private <T> T makeTeamCityRestCall(String url, Class<T> type) {
        return contact.makeJsonRestCall(url, type);
    }
}
