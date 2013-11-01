package org.netmelody.cieye.server.response.responder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.server.CiSpyIntermediary;
import org.netmelody.cieye.server.TargetGroupBriefing;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.Prison;
import org.netmelody.cieye.server.response.responder.LandscapeObservationResponder;

public final class LandscapeObservationResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiSpyIntermediary spyIntermediary = context.mock(CiSpyIntermediary.class);
    
    private final Feature feature = new Feature("F", "E", new CiServerType("J"));
    
    private final LandscapeObservationResponder responder = new LandscapeObservationResponder(new Landscape("L", feature),
                                                                                              spyIntermediary,
                                                                                              new Prison());
    
    @Test public void
    respondsWithCorrectJsonWhenNoTargetsArePresent() throws IOException {
        final TargetGroupBriefing briefing = new TargetGroupBriefing(new TargetDetailGroup(), 0L);
        
        context.checking(new Expectations() {{
            allowing(spyIntermediary).briefingOn(feature); will(returnValue(briefing));
        }});
        
        final CiEyeResponse response = responder.respond(null);
        assertThat(IOUtils.toString(response.inputStream()), startsWith("{\"targets\":[]}"));
    }

}
