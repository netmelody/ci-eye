package org.netmelody.cieye.spies.jenkins.test;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.JenkinsSpy;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.Server;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.spies.jenkins.jsondomain.ViewDetail;

import com.google.common.collect.Lists;

public final class JenkinsSpyTest {

    private final Mockery context = new Mockery();
    
    private final KnownOffendersDirectory detective = context.mock(KnownOffendersDirectory.class);
    private final Contact contact = context.mock(Contact.class);
    
    @Test public void
    lazilyRetrievesJobDetails() {
        final JenkinsSpy spy = new JenkinsSpy("myEndpoint", detective, contact);
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall(with(any(String.class)), with(Server.class));
                will(returnValue(serverWithViewsNamed("myFeatureName")));
            allowing(contact).makeJsonRestCall(with(any(String.class)), with(ViewDetail.class));
                will(returnValue(viewWithJobsNamed("myJob")));
            
            never(contact).makeJsonRestCall(with(any(String.class)), with(JobDetail.class));
        }});
        
        final Feature feature = new Feature("myFeatureName", "myEndpoint", new CiServerType("JENKINS"));
        final TargetDigestGroup targets = spy.targetsConstituting(feature);
        context.assertIsSatisfied();
        
        context.checking(new Expectations() {{
            oneOf(contact).makeJsonRestCall(with(any(String.class)), with(JobDetail.class));
                will(returnValue(new JobDetail()));
        }});
        spy.statusOf(targets.iterator().next().id());
        context.assertIsSatisfied();
    }
    
    private Server serverWithViewsNamed(String... names) {
        final Server server = new Server();
        server.views = Lists.newArrayList();
        for (String name : names) {
            final View view = new View();
            view.name = name;
            view.url = name;
            server.views.add(view);
        }
        return server;
    }
    
    private ViewDetail viewWithJobsNamed(String... names) {
        final ViewDetail server = new ViewDetail();
        server.jobs = Lists.newArrayList();
        for (String name : names) {
            final Job job = new Job();
            job.name = name;
            job.url = name;
            job.color = "red";
            server.jobs.add(job);
        }
        return server;
    }
}
