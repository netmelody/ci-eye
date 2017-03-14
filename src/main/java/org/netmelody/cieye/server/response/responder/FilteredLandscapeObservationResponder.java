package org.netmelody.cieye.server.response.responder;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.response.Prison;

public final class FilteredLandscapeObservationResponder extends LandscapeObservationResponder {

    private final Sponsor sponsor;

    public FilteredLandscapeObservationResponder(Landscape landscape, CiSpyIntermediary spyIntermediary, Prison prison, Sponsor sponsor) {
        super(landscape, spyIntermediary, prison);
        this.sponsor = sponsor;
    }

    protected LandscapeObservation filter(LandscapeObservation result) {
        return result.forSponsor(sponsor);
    }

}
