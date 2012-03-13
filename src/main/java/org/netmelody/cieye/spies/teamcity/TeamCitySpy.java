package org.netmelody.cieye.spies.teamcity;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.netmelody.cieye.core.domain.Status.UNKNOWN;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Project;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class TeamCitySpy implements CiSpy {

    private final TeamCityCommunicator communicator;
    private final BuildTypeAnalyser buildTypeAnalyser;

    private final Map<TargetId, BuildType> recognisedBuildTypes = newHashMap();

    public TeamCitySpy(final String endpoint, final CommunicationNetwork network, final KnownOffendersDirectory detective) {
        this.communicator = new TeamCityCommunicator(network, endpoint);
        this.buildTypeAnalyser = new BuildTypeAnalyser(this.communicator, detective);
    }

    @Override
    public TargetDigestGroup targetsConstituting(final Feature feature) {
        final Collection<BuildType> buildTypes = buildTypesFor(feature);
        final List<TargetDigest> digests = newArrayList();

        for (final BuildType buildType : buildTypes) {
            final TargetDigest targetDigest = new TargetDigest(communicator.endpoint() + buildType.href, buildType.webUrl(), buildType.name, UNKNOWN);
            digests.add(targetDigest);
            recognisedBuildTypes.put(targetDigest.id(), buildType);
        }

        return new TargetDigestGroup(digests);
    }

    @Override
    public TargetDetail statusOf(final TargetId target) {
        final BuildType buildType = recognisedBuildTypes.get(target);
        if (null == buildType) {
            return null;
        }
        return buildTypeAnalyser.targetFrom(buildType);
    }

    @Override
    public boolean takeNoteOf(final TargetId target, final String note) {
        if (!recognisedBuildTypes.containsKey(target)) {
            return false;
        }

        final BuildTypeDetail buildTypeDetail = communicator.detailsFor(recognisedBuildTypes.get(target));
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
        final Iterable<Project> projects = Iterables.filter(communicator.projects(),withNameOrNameIsEmpty(feature.name()));


        return communicator.buildTypesFor(projects);
    }

    private Predicate<Project> withNameOrNameIsEmpty(final String featureName) {
        return new Predicate<Project>() {
            @Override public boolean apply(final Project project) {
                return project.name.trim().equals(featureName.trim()) || featureName.equals("");
            }
        };
    }
}
