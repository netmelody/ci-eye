package org.netmelody.cieye.spies.jenkins.test;

import java.io.File;

import org.junit.Test;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.configuration.SettingsFile;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequesterBuilder;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.JobLaboratory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;

public final class JobLaboratoryTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final JenkinsCommunicator communicator = new JenkinsCommunicator("http://ci.jenkins-ci.org", new JsonRestRequesterBuilder(), "", "");
        final JobLaboratory lab = new JobLaboratory(communicator, new RecordedKnownOffenders(new SettingsFile(new File(""))));

        final Job job = new Job();
        job.url = "http://ci.jenkins-ci.org/view/Jenkins%20core/job/jenkins_pom/";
        
        lab.analyseJob(job);
        lab.lastBadBuildUrlFor(job);
    }
}
