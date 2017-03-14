package org.netmelody.cieye.server.response.responder;

import static java.lang.Math.min;

import java.io.IOException;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.TargetGroupBriefing;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.JsonTranslator;
import org.netmelody.cieye.server.response.Prison;
import org.simpleframework.http.Request;

public final class LandscapeObservationResponder implements CiEyeResponder {

    private final CiSpyIntermediary spyIntermediary;
    private final Landscape landscape;
    private final Prison prison;
    private final Function<LandscapeObservation, LandscapeObservation> converter;

    public LandscapeObservationResponder(Landscape landscape, CiSpyIntermediary spyIntermediary, Prison prison) {
        this(landscape, spyIntermediary, prison, Functions.<LandscapeObservation>identity());
    }

    public LandscapeObservationResponder(Landscape landscape, CiSpyIntermediary spyIntermediary, Prison prison, Function<LandscapeObservation, LandscapeObservation> converter) {
        this.landscape = landscape;
        this.spyIntermediary = spyIntermediary;
        this.prison = prison;
        this.converter = converter;
    }

    public CiEyeResponse respond(Request request) throws IOException {
        LandscapeObservation result = new LandscapeObservation();
        long timeToLiveMillis = Long.MAX_VALUE;
        for (Feature feature : landscape.features()) {
            final TargetGroupBriefing briefing = spyIntermediary.briefingOn(feature);
            result = result.add(briefing.status);
            timeToLiveMillis = min(timeToLiveMillis, briefing.millisecondsUntilNextUpdate);
        }

        if (prison.crimeReported(landscape)) {
            result = result.withDoh(prison.prisonersFor(landscape));
        }

        return CiEyeResponse.withJson(new JsonTranslator().toJson(converter.apply(result))).expiringInMillis(timeToLiveMillis);
    }
}
