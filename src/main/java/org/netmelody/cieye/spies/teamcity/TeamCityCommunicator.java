package org.netmelody.cieye.spies.teamcity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypes;
import org.netmelody.cieye.spies.teamcity.jsondomain.Builds;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Investigation;
import org.netmelody.cieye.spies.teamcity.jsondomain.Investigations;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;
import org.netmelody.cieye.spies.teamcity.jsondomain.ProjectDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.TeamCityProjects;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Iterables.find;

public final class TeamCityCommunicator {

    private final Contact contact;
    private final String endpoint;
    private final String prefix;

    public TeamCityCommunicator(Contact contact, String endpoint) {
        this.contact = contact;
        this.endpoint = endpoint;
        this.prefix = (contact.privileged() ? "/httpAuth" : "/guestAuth") + "/app/rest";
    }
    
    public String endpoint() {
        return this.endpoint;
    }

    public boolean canSpeakFor(Feature feature) {
        return endpoint.equals(feature.endpoint());
    }

    public Collection<Project> projects() {
        return makeTeamCityRestCall(endpoint + prefix + "/projects", TeamCityProjects.class).project();
    }

    public Collection<BuildType> buildTypes() {
        return makeTeamCityRestCall(endpoint + prefix + "/buildTypes", BuildTypes.class).buildType();
    }

    public Collection<BuildType> buildTypesFor(Project projectDigest) {
        return makeTeamCityRestCall(endpoint + projectDigest.href, ProjectDetail.class).buildTypes.buildType();
    }

    public BuildTypeDetail detailsFor(BuildType buildType) {
        return makeTeamCityRestCall(endpoint + buildType.href, BuildTypeDetail.class);
    }

    public Build lastCompletedBuildFor(BuildTypeDetail buildTypeDetail) {
        final Builds completedBuilds = makeTeamCityRestCall(endpoint + buildTypeDetail.builds.href, Builds.class);
        if (null == completedBuilds.build() || completedBuilds.build().isEmpty()) {
            return null;
        }
        return find(completedBuilds.build(), alwaysTrue());
    }

    public List<Build> runningBuildsFor(BuildType buildType) {
        return makeTeamCityRestCall(endpoint + prefix + "/builds/?locator=running:true,buildType:id:" + buildType.id, Builds.class).build();
    }

    public List<Investigation> investigationsOf(BuildType buildType) {
        return makeTeamCityRestCall(endpoint + buildType.href + "/investigations", Investigations.class).investigation();
    }

    public BuildDetail detailsOf(Build build) {
        return makeTeamCityRestCall(endpoint + build.href, BuildDetail.class);
    }

    public void commentOn(Build lastCompletedBuild, String note) {
        contact.doPut(endpoint + lastCompletedBuild.href + "/comment", note);
    }

    public List<Change> changesOf(BuildDetail buildDetail) {
        final JsonElement json = contact.makeJsonRestCall(endpoint + buildDetail.changes.href);
        final JsonElement change = json.isJsonObject() ? json.getAsJsonObject().get("change") : JsonNull.INSTANCE;
        
        if (null == change || !(change.isJsonArray() || change.isJsonObject())) {
            return ImmutableList.of();
        }
        
        final Gson gson = new Gson();
        final List<Change> changes = new ArrayList<Change>();
        final Iterable<JsonElement> changesJson = change.isJsonArray() ? change.getAsJsonArray() : ImmutableList.of(change);
        for (JsonElement jsonElement : changesJson) {
            changes.add(gson.fromJson(jsonElement, Change.class));
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
