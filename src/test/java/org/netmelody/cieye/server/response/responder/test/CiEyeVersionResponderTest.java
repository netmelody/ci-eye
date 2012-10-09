package org.netmelody.cieye.server.response.responder.test;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.CiEyeServerInformationFetcher;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.netmelody.cieye.server.response.responder.CiEyeVersionResponder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class CiEyeVersionResponderTest {

    private final Mockery context = new Mockery();
    
    private final CiEyeServerInformationFetcher configFetcher = context.mock(CiEyeServerInformationFetcher.class);
    private final CiEyeNewVersionChecker updateChecker = context.mock(CiEyeNewVersionChecker.class);
    
    private final CiEyeVersionResponder ciEyeVersionResponder = new CiEyeVersionResponder(configFetcher, updateChecker);
    
    @Test public void
    providesJsonWithCurrentVersionInformation() throws IOException {
        context.checking(new Expectations() {{
            allowing(configFetcher).getVersion(); will(returnValue("myVersion"));
            ignoring(updateChecker);
        }});
        
        final CiEyeResponse response = ciEyeVersionResponder.respond(null);
        assertThat(IOUtils.toString(response.inputStream()), containsString("\"currentServerVersion\":\"myVersion\""));
    }
    
    @Test public void
    providesJsonWithLatestVersionInformation() throws IOException {
        context.checking(new Expectations() {{
            allowing(updateChecker).getLatestVersion(); will(returnValue("myNewVersion"));
            ignoring(configFetcher);
        }});
        
        final CiEyeResponse response = ciEyeVersionResponder.respond(null);
        
        assertThat(IOUtils.toString(response.inputStream()), containsString("\"latestServerVersion\":\"myNewVersion\""));
    }

}
