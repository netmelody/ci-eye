package org.netmelody.cieye.server.observation.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;
import org.netmelody.cieye.server.observation.GovernmentReport;

public final class GovernmentReportTest {

    private final Mockery context = new Mockery();
    
    private final CiEyeNewVersionChecker watchdog = context.mock(CiEyeNewVersionChecker.class);
    private final GovernmentReport governmentReport = new GovernmentReport(watchdog);

    @Test public void
    respondsWithDataFromUnderlyingWatchdog() {
        context.checking(new Expectations() {{
            allowing(watchdog).getLatestVersion(); will(returnValue("versionA"));
        }});
        
        final String result = governmentReport.getLatestVersion();
        assertThat(result, is("versionA"));
    }
    
    @Test public void
    cachesRequestsToTheUnderlyingWatchdog() {
        context.checking(new Expectations() {{
            oneOf(watchdog).getLatestVersion();
        }});
        
        governmentReport.getLatestVersion();
        context.assertIsSatisfied();
        
        governmentReport.getLatestVersion();
    }

}
