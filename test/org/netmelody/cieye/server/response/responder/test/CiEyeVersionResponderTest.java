package org.netmelody.cieye.server.response.responder.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.responder.CiEyeVersionResponder;
import org.simpleframework.http.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CiEyeVersionResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiEyeServerInformationFetcher configFetcher = context.mock(CiEyeServerInformationFetcher.class);
    
    private final CiEyeVersionResponder ciEyeVersionResponder = new CiEyeVersionResponder(configFetcher);
    
    @Test public void
    providesJsonWithVersionInformation() throws IOException {
        final Response response = context.mock(Response.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(output);

        context.checking(new Expectations() {{
            allowing(configFetcher).getVersion(); will(returnValue("myVersion"));
            
            allowing(response).getPrintStream(); will(returnValue(printStream));
            ignoring(response);
        }});
        
        ciEyeVersionResponder.writeTo(response);
        
        assertThat(output.toString(), is("{\"currentServerVersion\":\"myVersion\"}\n"));
    }

}
