package org.netmelody.cieye.spies.jenkins.test;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.JobLaboratory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Build;
import org.netmelody.cieye.spies.jenkins.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class JobLaboratoryTest {

    private final Mockery context = new Mockery();
    
    private final Contact contact = context.mock(Contact.class);
    private final KnownOffendersDirectory directory = context.mock(KnownOffendersDirectory.class);
    
    private final JobLaboratory jobLab = new JobLaboratory(new JenkinsCommunicator("ep", "user", "pass", contact), directory);
    private final Job job = defaultJob();
    
    @Test public void
    returnsInstantlyForAGreenJobThatIsNotBuilding() {
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall("jobUrl/api/json", JobDetail.class); will(returnValue(new JobDetail()));
        }});
        
        TargetDetail target = jobLab.analyseJob(job);
        
        assertThat(target.status(), Matchers.is(Status.GREEN));
    }

    @Test public void
    includesLastStartTimeForGreenBuild() {
        final JobDetail jobDetail = new JobDetail();
        jobDetail.lastBuild = new Build();
        jobDetail.lastBuild.url = "buildUrl";
        final BuildDetail buildDetail = new BuildDetail();
        buildDetail.timestamp = 100L;
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall("jobUrl/api/json", JobDetail.class); will(returnValue(jobDetail));
            allowing(contact).makeJsonRestCall("buildUrl/api/json", BuildDetail.class); will(returnValue(buildDetail));
        }});
        
        TargetDetail target = jobLab.analyseJob(job);
        
        assertThat(target.lastStartTime(), is(100L));
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
