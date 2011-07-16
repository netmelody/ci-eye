package org.netmelody.cieye.spies.teamcity.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.text.SimpleDateFormat;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.teamcity.TeamCitySpy;
import org.netmelody.cieye.spies.teamcity.jsondomain.TeamCityProjects;


public final class TeamCitySpyTest {

    private final Mockery context = new Mockery();
    
    private final CommunicationNetwork network = context.mock(CommunicationNetwork.class);
    private final KnownOffendersDirectory detective = context.mock(KnownOffendersDirectory.class);
    private final Contact contact = context.mock(Contact.class);
    
    @Before
    public void setup() {
        context.checking(new Expectations() {{
            allowing(network).makeContact(with(any(SimpleDateFormat.class))); will(returnValue(contact));
        }});
    }
    
    @Test public void
    givesEmptyStatusForAnUnknownEndpoint() {
        final TeamCitySpy spy = new TeamCitySpy("myEndpoint", network, detective);
        
        final TargetGroup result = spy.statusOf(new Feature("", "myOtherEndpoint", new CiServerType("TEAMCITY")));
        
        assertThat(result.targets(), is(Matchers.<Target>empty()));
    }
    
    @Test public void
    logsInUsingGuestAccess() {
        final TeamCitySpy spy = new TeamCitySpy("myEndpoint", network, detective);
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall(with(any(String.class)), with(TeamCityProjects.class));
                will(returnValue(new TeamCityProjects()));
            
            oneOf(contact).performBasicLogin("myEndpoint/guestAuth/");
        }});
        
        spy.statusOf(new Feature("", "myEndpoint", new CiServerType("TEAMCITY")));
        
        context.assertIsSatisfied();
    }
}
