package org.netmelody.cieye.spies.teamcity;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Iterables.find;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Builds;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Changes;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;
import org.netmelody.cieye.spies.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.TeamCityProjects;

public final class TeamCityCommunicator {

    private final Contact contact;
    private final String endpoint;

    public TeamCityCommunicator(final CommunicationNetwork network, final String endpoint) {
        this.contact = network.makeContact(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"));
        this.endpoint = endpoint;
    }

    public String endpoint() {
        return this.endpoint;
    }

    public boolean canSpeakFor(final Feature feature) {
        return endpoint.equals(feature.endpoint());
    }

    public void loginAsGuest() {
        contact.performBasicLogin(endpoint + "/guestAuth/");
    }

    public Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + "/app/rest/projects", TeamCityProjects.class).project();
    }

    public Collection<BuildType> buildTypesFor(final Iterable<Project> projects) {
  final Collection<BuildType>  buildTypes = new ArrayList<BuildType>();

        for ( final Project project : projects) {
        buildTypes.addAll(makeTeamCityRestCall(endpoint + project.href, ProjectDetail.class).buildTypes.buildType());
        }
        return buildTypes;
    }

    public BuildTypeDetail detailsFor(final BuildType buildType) {
        return makeTeamCityRestCall(endpoint + buildType.href, BuildTypeDetail.class);
    }

    public Build lastCompletedBuildFor(final BuildTypeDetail buildTypeDetail) {
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (null == completedBuilds.build() || completedBuilds.build().isEmpty()) {
            return null;
        }
        return find(completedBuilds.build(), alwaysTrue());
    }

    public List<Build> runningBuildsFor(final BuildType buildType) {
        return makeTeamCityRestCall(endpoint + "/app/rest/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class).build();
    }

    public BuildDetail detailsOf(final Build build) {
        return makeTeamCityRestCall(endpoint + build.href, BuildDetail.class);
    }

    public void commentOn(final Build lastCompletedBuild, final String note) {
        contact.performBasicAuthentication("cieye", "cieye");
        contact.doPut(endpoint + lastCompletedBuild.href + "/comment", note);
    }

    public List<Change> changesOf(final BuildDetail buildDetail) {
        final List<Change> changes = new ArrayList<Change>();

            changes.addAll(makeTeamCityRestCall(endpoint + buildDetail.changes.href, Changes.class).change());
        return changes;
    }

    public ChangeDetail detailedChangesOf(final Change change) {
        return makeTeamCityRestCall(endpoint + change.href, ChangeDetail.class);
    }

    private <T> T makeTeamCityRestCall(final String url, final Class<T> type) {
        return contact.makeJsonRestCall(url, type);
    }
}
