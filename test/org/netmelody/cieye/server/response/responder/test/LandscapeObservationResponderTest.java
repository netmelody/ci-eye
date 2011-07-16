package org.netmelody.cieye.server.response.responder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.server.CiSpyAllocator;
import org.netmelody.cieye.server.response.Prison;
import org.netmelody.cieye.server.response.responder.LandscapeObservationResponder;
import org.simpleframework.http.Response;

public final class LandscapeObservationResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiSpyAllocator spyAllocator = context.mock(CiSpyAllocator.class);
    
    private final Feature feature = new Feature("F", "E", new CiServerType("J"));
    
    private final LandscapeObservationResponder responder = new LandscapeObservationResponder(new Landscape("L", feature),
                                                                                              spyAllocator,
                                                                                              new Prison());
    
    @Test public void
    respondsWithCorrectJsonWhenNoTargetsArePresent() throws IOException {
        final Response response = context.mock(Response.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(output);
        final CiSpy spy = context.mock(CiSpy.class);
        final TargetGroup targets = new TargetGroup();
        
        context.checking(new Expectations() {{
            allowing(response).getPrintStream(); will(returnValue(printStream));
            ignoring(response);
            
            allowing(spyAllocator).spyFor(feature); will(returnValue(spy));
            allowing(spy).statusOf(feature); will(returnValue(targets));
            ignoring(spy);
        }});
        
        responder.writeTo(response);
        
        assertThat(output.toString(), is("{\"targets\":[]}\r\n"));
    }

}
