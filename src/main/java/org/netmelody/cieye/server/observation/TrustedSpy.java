package org.netmelody.cieye.server.observation;

import java.util.Map;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Maps.newHashMap;

public final class TrustedSpy implements CiSpy {

    private final CiSpy untrustedSpy;
    private final Map<TargetId, TargetDigest> emptyFeatureTargets = newHashMap();

    public TrustedSpy(CiSpy untrustedSpy) {
        this.untrustedSpy = untrustedSpy;
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        final TargetDigestGroup untrustedResult = untrustedSpy.targetsConstituting(feature);
        final TargetDigestGroup result = (untrustedResult == null) ? new TargetDigestGroup() : untrustedResult;
        
        if (result.isEmpty()) {
            final TargetId noTargetId = new TargetId("UNK_" + feature.endpoint() + feature.name());
            TargetDigest noTarget = emptyFeatureTargets.get(noTargetId);
            if (null == noTarget) {
                noTarget = new TargetDigest(noTargetId.id(), feature.endpoint(), "EMPTY: " + feature.name(), Status.BROKEN);
                emptyFeatureTargets.put(noTargetId, noTarget);
            }
            return new TargetDigestGroup(ImmutableList.of(noTarget));
        }
        return result;
    }

    @Override
    public TargetDetail statusOf(TargetId targetId) {
        if (emptyFeatureTargets.containsKey(targetId)) {
            final TargetDigest noTarget = emptyFeatureTargets.get(targetId);
            return new TargetDetail(targetId.id(), noTarget.webUrl(), noTarget.name(), noTarget.status(), 0L);
        }
        final TargetDetail untrustedResult = untrustedSpy.statusOf(targetId);
        return (untrustedResult == null) ? new TargetDetail(targetId.id(), "", "", Status.UNKNOWN, 0L) : untrustedResult;
    }

    @Override
    public boolean takeNoteOf(TargetId target, String note) {
        return untrustedSpy.takeNoteOf(target, note);
    }
}