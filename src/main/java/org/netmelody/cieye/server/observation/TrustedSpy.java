package org.netmelody.cieye.server.observation;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetId;
import org.netmelody.cieye.core.observation.CiSpy;

public final class TrustedSpy implements CiSpy {

    private final CiSpy untrustedSpy;

    public TrustedSpy(CiSpy untrustedSpy) {
        this.untrustedSpy = untrustedSpy;
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        final TargetDigestGroup untrustedResult = untrustedSpy.targetsConstituting(feature);
        return (untrustedResult == null) ? new TargetDigestGroup() : untrustedResult;
    }

    @Override
    public TargetDetail statusOf(TargetId target) {
        final TargetDetail untrustedResult = untrustedSpy.statusOf(target);
        return (untrustedResult == null) ? new TargetDetail(target.id(), "", "", Status.UNKNOWN, 0L) : untrustedResult;
    }

    @Override
    public boolean takeNoteOf(TargetId target, String note) {
        return untrustedSpy.takeNoteOf(target, note);
    }
}