package org.netmelody.cieye.spies.jenkins.test;

import com.google.common.base.Functions;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.server.observation.test.StubGrapeVine;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.JobAnalyser;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;

import java.io.IOException;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public final class JobAnalyserTest {
    private final Mockery context = new Mockery();

    private final KnownOffendersDirectory detective = context.mock(KnownOffendersDirectory.class);
    private final StubGrapeVine channel = new StubGrapeVine();

    private JobAnalyser analyser;

    @Before
    public void setup() {
        analyser = new JobAnalyser(new JenkinsCommunicator("http://jenkins", new JsonRestRequester(new Gson(), Functions.<String>identity(), channel)),
                                                "http://jenkins/job/foo", detective);
    }

    @Test public void
    canAnalyseAJenkinsJob() {
        final Job job = jobDigest();

        channel.respondingWith("http://jenkins/job/foo/api/json", contentFrom("jenkins_2.60.1_jobdetail_1.json"));
        channel.respondingWith("http://jenkins/job/foo/1/api/json", contentFrom("jenkins_2.60.1_builddetail_1.json"));
        channel.respondingWith("http://jenkins/job/foo/2/api/json", contentFrom("jenkins_2.60.1_builddetail_2.json"));

        context.checking(new Expectations() {{
            allowing(detective).search(with(containsString("magic")));
                will(returnValue(Sets.newHashSet(new Sponsor("Bob"))));
        }});

        TargetDetail analysis = analyser.analyse(job);
        context.assertIsSatisfied();

        Set<Sponsor> sponsors = analysis.sponsors();
        assertThat(sponsors, is(Matchers.<Sponsor>iterableWithSize(1)));
        assertThat(sponsors.iterator().next().name(), is("Bob"));
    }

    private Job jobDigest() {
        final Job job = new Job();
        job.url = "http://jenkins/job/foo";
        return job;
    }

    private String contentFrom(String resourceName) {
        try {
            return IOUtils.toString(JobAnalyserTest.class.getResourceAsStream(resourceName));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}