package org.netmelody.cieye.spies.jenkins.test;

import java.io.File;

import org.junit.Test;
import org.netmelody.cieye.core.domain.*;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.configuration.SettingsFile;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.JenkinsSpy;
import org.netmelody.cieye.spies.jenkins.JobLaboratory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;

import com.google.gson.GsonBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public final class JenkinsSpyImmersionTest {

    @Test public void
    canAnalyseJobFromTheJenkinsLiveInstance() {
        final Contact contact = new JsonRestRequester(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create());
        final JenkinsCommunicator communicator = new JenkinsCommunicator("http://ci.jenkins-ci.org", contact);
        final JobLaboratory lab = new JobLaboratory(communicator, new RecordedKnownOffenders(new SettingsFile(new File(""))));

        final Job job = new Job();
        job.url = "http://ci.jenkins-ci.org/view/Jenkins%20core/job/jenkins_pom/";
        
        lab.analyseJob(job);
        lab.lastBadBuildUrlFor(job);
    }

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final Contact realContact = new JsonRestRequester(builder.create());
        final JenkinsSpy witness = new JenkinsSpy("http://ci.jenkins-ci.org", new RecordedKnownOffenders(new SettingsFile(new File(""))), realContact);
        final Feature feature = new Feature("Jenkins core", "http://ci.jenkins-ci.org", new CiServerType("JENKINS"));

        final TargetDigestGroup digests = witness.targetsConstituting(feature);
        
        assertThat(witness.statusOf(digests.iterator().next().id(), feature.showPersonalBuilds()), is(notNullValue(TargetDetail.class)));
    }

    @Test public void
    canPullFromSecureJenkinsLiveInstance() {
        final GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final Contact realContact = new JsonRestRequester(builder.create());
        final JenkinsSpy witness = new JenkinsSpy("https://jenkins.puppetlabs.com", new RecordedKnownOffenders(new SettingsFile(new File(""))), realContact);
        final Feature feature = new Feature("Known Good", "https://jenkins.puppetlabs.com", new CiServerType("JENKINS"));
        final TargetDigestGroup digests = witness.targetsConstituting(feature);
        
        assertThat(witness.statusOf(digests.iterator().next().id(), feature.showPersonalBuilds()), is(notNullValue(TargetDetail.class)));
    }
}
