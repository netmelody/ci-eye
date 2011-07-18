package org.netmelody.cieye.spies.jenkins.jsondomain.test;

import org.junit.Ignore;
import org.junit.Test;
import org.netmelody.cieye.spies.jenkins.jsondomain.Build;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class JobDetailTest {

    @Test public void
    correctlyDeterminesTheLastFailedBuildUrl() {
        final JobDetail jobDetail = new JobDetail();
        jobDetail.lastUnstableBuild = build(123, "http://blah/455");
        jobDetail.lastUnsuccessfulBuild = build(9, "http://blah/454");
        jobDetail.lastFailedBuild = build(456, "http://blah/456");
        
        assertThat(jobDetail.lastBadBuildUrl(), is("http://blah/456"));
    }
    
    @Ignore("excites bug that needs fixing asap")
    @Test public void
    correctlyDeterminesTheLastFailedBuildUrlWithNonLexographicUrls() {
        final JobDetail jobDetail = new JobDetail();
        jobDetail.lastUnstableBuild = build(123, "http://blah/123");
        jobDetail.lastUnsuccessfulBuild = build(9, "http://blah/9");
        jobDetail.lastFailedBuild = build(456, "http://blah/456");
        
        assertThat(jobDetail.lastBadBuildUrl(), is("http://blah/456"));
    }

    private Build build(long number, String url) {
        final Build build = new Build();
        build.number = number;
        build.url = url;
        return build;
    }

}
