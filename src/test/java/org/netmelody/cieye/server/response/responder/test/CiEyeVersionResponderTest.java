package org.netmelody.cieye.server.response.responder.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.responder.CiEyeVersionResponder;
import org.simpleframework.http.Response;

public class CiEyeVersionResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiEyeServerInformationFetcher configFetcher = context.mock(CiEyeServerInformationFetcher.class);
    private final CiEyeNewVersionChecker updateChecker = context.mock(CiEyeNewVersionChecker.class);
    
    private final CiEyeVersionResponder ciEyeVersionResponder = new CiEyeVersionResponder(configFetcher, updateChecker);
    
    @Test public void
    providesJsonWithCurrentVersionInformation() throws IOException {
        final Response response = context.mock(Response.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(output);

        context.checking(new Expectations() {{
            allowing(configFetcher).getVersion(); will(returnValue("myVersion"));
            allowing(response).getPrintStream(); will(returnValue(printStream));
            ignoring(response);
            ignoring(updateChecker);
        }});
        
        ciEyeVersionResponder.writeTo(response);
        
        assertThat(output.toString(), containsString("\"currentServerVersion\":\"myVersion\""));
    }
    
    @Test public void
    providesJsonWithLatestVersionInformation() throws IOException {
        final Response response = context.mock(Response.class);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(output);

        context.checking(new Expectations() {{
            allowing(updateChecker).getLatestVersion(); will(returnValue("myNewVersion"));
            allowing(response).getPrintStream(); will(returnValue(printStream));
            ignoring(response);
            ignoring(configFetcher);
        }});
        
        ciEyeVersionResponder.writeTo(response);
        
        assertThat(output.toString(), containsString("\"latestServerVersion\":\"myNewVersion\""));
    }

}
