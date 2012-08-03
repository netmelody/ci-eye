package org.netmelody.cieye.spies.teamcity;

import static com.google.common.collect.Collections2.filter;
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
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.jsondomain.Build;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildType;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildTypeDetail;

import com.google.common.base.Predicate;

public final class TeamCitySpy implements CiSpy {

    private final TeamCityCommunicator communicator;
    private final BuildTypeAnalyser buildTypeAnalyser;

    private final Map<TargetId, BuildType> recognisedBuildTypes = newHashMap();
    
    public TeamCitySpy(String endpoint, KnownOffendersDirectory detective, Contact contact) {
        this.communicator = new TeamCityCommunicator(contact, endpoint);
        this.buildTypeAnalyser = new BuildTypeAnalyser(this.communicator, detective);
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        final Collection<BuildType> buildTypes = buildTypesFor(feature);
        final List<TargetDigest> digests = newArrayList();
        
        for (BuildType buildType : buildTypes) {
            final TargetDigest targetDigest = new TargetDigest(communicator.endpoint() + buildType.href, buildType.webUrl(), buildType.name, UNKNOWN);
            digests.add(targetDigest);
            recognisedBuildTypes.put(targetDigest.id(), buildType);
        }
        
        return new TargetDigestGroup(digests);
    }

    @Override
    public TargetDetail statusOf(final TargetId target) {
        BuildType buildType = recognisedBuildTypes.get(target);
        if (null == buildType) {
            return null;
        }
        return buildTypeAnalyser.targetFrom(buildType);
    }
    
    @Override
    public boolean takeNoteOf(TargetId target, String note) {
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
        
        final Collection<BuildType> buildTypes = communicator.buildTypes();
        if (feature.name().isEmpty()) {
            return buildTypes;
        }
        
        return filter(buildTypes, withFeatureName(feature.name()));
    }

    private Predicate<BuildType> withFeatureName(final String featureName) {
        return new Predicate<BuildType>() {
            @Override public boolean apply(BuildType buildType) {
                return buildType.projectName.trim().equals(featureName.trim());
            }
        };
    }
}
