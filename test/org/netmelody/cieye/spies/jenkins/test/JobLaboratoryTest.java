package org.netmelody.cieye.spies.jenkins.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.configuration.SettingsFile;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.JobLaboratory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import com.google.gson.GsonBuilder;

public final class JobLaboratoryTest {

    private final Mockery context = new Mockery();
    
    private final Contact contact = context.mock(Contact.class);
    private final KnownOffendersDirectory directory = context.mock(KnownOffendersDirectory.class);
    
    private final JobLaboratory jobLab = new JobLaboratory(new JenkinsCommunicator("ep", "user", "pass", contact), directory);
    private final Job job = defaultJob();
    
    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final Contact contact = new JsonRestRequester(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create());
        final JenkinsCommunicator communicator = new JenkinsCommunicator("http://ci.jenkins-ci.org", "", "", contact);
        final JobLaboratory lab = new JobLaboratory(communicator, new RecordedKnownOffenders(new SettingsFile(new File(""))));

        job.url = "http://ci.jenkins-ci.org/view/Jenkins%20core/job/jenkins_pom/";
        
        lab.analyseJob(job);
        lab.lastBadBuildUrlFor(job);
    }
    
    @Test public void
    returnsInstantlyForAGreenJobThatIsNotBuilding() {
        
        TargetDetail target = jobLab.analyseJob(job);
        
        assertThat(target.status(), Matchers.is(Status.GREEN));
    }

    @Test public void
    alwaysAnalysesARedBuild() {
        job.color = "red";
        final JobDetail jobDetail = new JobDetail();
        jobDetail.color = "red";
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall("jobUrl/api/json", JobDetail.class); will(returnValue(jobDetail));
        }});
        
        TargetDetail target = jobLab.analyseJob(job);
        
        assertThat(target.status(), Matchers.is(Status.BROKEN));
    }
    
    private Job defaultJob() {
        final Job job = new Job();
        job.name = "jobName";
        job.url = "jobUrl";
        job.color = "blue";
        return job;
    }
}
