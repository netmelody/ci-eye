package org.netmelody.cieye.spies.jenkins.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetDetail;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.configuration.SettingsFile;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.spies.jenkins.JenkinsSpy;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.JobDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.Server;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.spies.jenkins.jsondomain.ViewDetail;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

public final class JenkinsSpyTest {

    private final Mockery context = new Mockery();
    
    private final KnownOffendersDirectory detective = context.mock(KnownOffendersDirectory.class);
    private final Contact contact = context.mock(Contact.class);
    
    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        final Contact realContact = new JsonRestRequester(builder.create());
        final JenkinsSpy witness = new JenkinsSpy("http://ci.jenkins-ci.org", new RecordedKnownOffenders(new SettingsFile(new File(""))), realContact);
        
        final TargetDigestGroup digests = witness.targetsConstituting(new Feature("Jenkins core", "http://ci.jenkins-ci.org", new CiServerType("JENKINS")));
        
        assertThat(witness.statusOf(digests.iterator().next().id()), is(notNullValue(TargetDetail.class)));
    }
    
    @Test public void
    lazilyRetrievesJobDetails() {
        final JenkinsSpy spy = new JenkinsSpy("myEndpoint", detective, contact);
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall(with(any(String.class)), with(Server.class));
                will(returnValue(serverWithViewsNamed("myFeatureName")));
            allowing(contact).makeJsonRestCall(with(any(String.class)), with(ViewDetail.class));
                will(returnValue(viewWithJobsNamed("myJob")));
            
            never(contact).makeJsonRestCall(with(any(String.class)), with(JobDetail.class));
            
            ignoring(contact).performBasicLogin(with(any(String.class)));
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
