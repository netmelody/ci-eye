package org.netmelody.cieye.spies.teamcity.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.server.observation.test.StubGrapeVine;
import org.netmelody.cieye.spies.teamcity.TeamCityCommunicator;
import org.netmelody.cieye.spies.teamcity.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.teamcity.jsondomain.Change;
import org.netmelody.cieye.spies.teamcity.jsondomain.ChangesHref;

import com.google.common.base.Functions;
import com.google.gson.Gson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class TeamCityCommunicatorTest {

    private final StubGrapeVine channel = new StubGrapeVine();

    private TeamCityCommunicator communicator;

    @Before
    public void setup() {
        communicator = new TeamCityCommunicator(new JsonRestRequester(new Gson(), Functions.<String>identity(), channel),
                                                "http://foo");
    }

    @Test public void
    requestsSingularBuildChangesForTeamCitySixApi() {
        final BuildDetail buildDetail = buildDetail(1);
        channel.respondingWith("http://foo" + buildDetail.changes.href, contentFrom("tc_6.5.5_changes_1.json").replace("@", ""));
        
        final List<Change> changes = communicator.changesOf(buildDetail);
        assertThat(changes, is(Matchers.<Change>iterableWithSize(1)));
        assertThat(changes.get(0).id, is("48834"));
    }

    @Test public void
    requestsMultipleBuildChangesForTeamCitySixApi() {
        final BuildDetail buildDetail = buildDetail(2);
        channel.respondingWith("http://foo" + buildDetail.changes.href, contentFrom("tc_6.5.5_changes_2.json").replace("@", ""));

        final List<Change> changes = communicator.changesOf(buildDetail);
        assertThat(changes, is(Matchers.<Change>iterableWithSize(2)));
        assertThat(changes.get(0).id, is("47951"));
        assertThat(changes.get(1).id, is("47949"));
    }

    @Test public void
    requestsSingularBuildChangesForTeamCitySevenApi() {
        final BuildDetail buildDetail = buildDetail(1);
        channel.respondingWith("http://foo" + buildDetail.changes.href, contentFrom("tc_7.0.0_changes_1.json"));
        
        final List<Change> changes = communicator.changesOf(buildDetail);
        assertThat(changes, is(Matchers.<Change>iterableWithSize(1)));
        assertThat(changes.get(0).id, is("62889"));
    }

    @Test public void
    requestsMultipleBuildChangesForTeamCitySevenApi() {
        final BuildDetail buildDetail = buildDetail(2);
        channel.respondingWith("http://foo" + buildDetail.changes.href, contentFrom("tc_7.0.0_changes_2.json"));
        
        final List<Change> changes = communicator.changesOf(buildDetail);
        assertThat(changes, is(Matchers.<Change>iterableWithSize(2)));
        assertThat(changes.get(0).id, is("62855"));
        assertThat(changes.get(1).id, is("62854"));
    }

    private BuildDetail buildDetail(int size) {
        final BuildDetail buildDetail = new BuildDetail();
        buildDetail.changes = new ChangesHref();
        buildDetail.changes.count = size;
        buildDetail.changes.href = "/app/rest/changes/id:12345";
        return buildDetail;
    }

    private String contentFrom(String resourceName) {
        try {
            return IOUtils.toString(TeamCityCommunicatorTest.class.getResourceAsStream(resourceName));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}