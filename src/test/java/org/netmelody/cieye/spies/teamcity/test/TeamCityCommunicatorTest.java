package org.netmelody.cieye.spies.teamcity.test;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.spies.teamcity.TeamCityCommunicator;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesHref;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesMany;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesOne;

import com.google.gson.Gson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TeamCityCommunicatorTest {

    private final Mockery context = new Mockery();
    
    private final CommunicationNetwork network = context.mock(CommunicationNetwork.class);
    private final Contact contact = context.mock(Contact.class);
    
    private TeamCityCommunicator communicator;
    
    @Before
    public void setup() {
        context.checking(new Expectations() {{
            allowing(network).makeContact(with(any(SimpleDateFormat.class))); will(returnValue(contact));
        }});
        communicator = new TeamCityCommunicator(network, "http://foo");
    }
    
    @Test public void
    requestsSingularBuildChangesForTeamCitySixApi() {
        final BuildDetail buildDetail = new BuildDetail();
        buildDetail.changes = new ChangesHref();
        buildDetail.changes.count = 1;
        buildDetail.changes.href = "/app/rest/changes/id:1";
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall("http://foo/app/rest/changes/id:1", ChangesOne.class);
                will(returnValue(new Gson().fromJson(streamFor("tc_6.5.5_changes_1.json"), ChangesOne.class)));
        }});
        
        final List<Change> changes = communicator.changesOf(buildDetail);
        
        assertThat(changes, is(Matchers.<Change>iterableWithSize(1)));
    }
    
    @Test public void
    requestsMultipleBuildChangesForTeamCitySixApi() {
        final BuildDetail buildDetail = new BuildDetail();
        buildDetail.changes = new ChangesHref();
        buildDetail.changes.count = 2;
        buildDetail.changes.href = "/app/rest/changes/id:1";
        
        context.checking(new Expectations() {{
            allowing(contact).makeJsonRestCall("http://foo/app/rest/changes/id:1", ChangesMany.class);
            will(returnValue(new Gson().fromJson(streamFor("tc_6.5.5_changes_2.json"), ChangesMany.class)));
        }});
        
        final List<Change> changes = communicator.changesOf(buildDetail);
        
        assertThat(changes, is(Matchers.<Change>iterableWithSize(2)));
    }
    
    private InputStreamReader streamFor(String jsonFile) {
        return new InputStreamReader(TeamCityCommunicatorTest.class.getResourceAsStream(jsonFile));
    }
}