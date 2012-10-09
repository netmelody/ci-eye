package org.netmelody.cieye.server.response.responder.test;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetDetailGroup;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.CiSpyHandler;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.Prison;
import org.netmelody.cieye.server.response.responder.LandscapeObservationResponder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

public final class LandscapeObservationResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiSpyAllocator spyAllocator = context.mock(CiSpyAllocator.class);
    
    private final Feature feature = new Feature("F", "E", new CiServerType("J"));
    
    private final LandscapeObservationResponder responder = new LandscapeObservationResponder(new Landscape("L", feature),
                                                                                              spyAllocator,
                                                                                              new Prison());
    
    @Test public void
    respondsWithCorrectJsonWhenNoTargetsArePresent() throws IOException {
        final CiSpyHandler spy = context.mock(CiSpyHandler.class);
        final TargetDetailGroup targets = new TargetDetailGroup();
        
        context.checking(new Expectations() {{
            allowing(spyAllocator).spyFor(feature); will(returnValue(spy));
            allowing(spy).statusOf(feature); will(returnValue(targets));
            ignoring(spy);
        }});
        
        final CiEyeResponse response = responder.respond(null);
        assertThat(IOUtils.toString(response.inputStream()), startsWith("{\"targets\":[]}"));
    }

}
