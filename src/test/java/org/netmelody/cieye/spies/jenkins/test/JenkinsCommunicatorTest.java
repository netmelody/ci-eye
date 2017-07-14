package org.netmelody.cieye.spies.jenkins.test;

import com.google.common.base.Functions;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequester;
import org.netmelody.cieye.server.observation.test.StubGrapeVine;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.jsondomain.BuildDetail;
import org.netmelody.cieye.spies.jenkins.jsondomain.ChangeSetItem;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public final class JenkinsCommunicatorTest {

    private final StubGrapeVine channel = new StubGrapeVine();

    private JenkinsCommunicator communicator;

    @Before
    public void setup() {
        communicator = new JenkinsCommunicator("http://jenkins", new JsonRestRequester(new Gson(), Functions.<String>identity(), channel));
    }

    @Test public void
    requestsSingularBuildChangesForJenkinsSvnBuild() {
        channel.respondingWith("http://jenkins/job/foo/2/api/json", contentFrom("jenkins_1.625.3_builddetail_2.json"));

        final BuildDetail buildDetail = communicator.buildDetailsFor("http://jenkins/job/foo/2");

        List<ChangeSetItem> changes = buildDetail.changeSet.items;

        assertThat(changes, is(Matchers.<ChangeSetItem>iterableWithSize(1)));
        assertThat(changes.get(0).msg, is("tomd: add readme"));
        assertThat(changes.get(0).comment, is(nullValue()));
    }

    @Test public void
    requestsSingularBuildChangesForJenkinsGitBuild() {
        channel.respondingWith("http://jenkins/job/foo/2/api/json", contentFrom("jenkins_2.60.1_builddetail_2.json"));

        final BuildDetail buildDetail = communicator.buildDetailsFor("http://jenkins/job/foo/2");

        List<ChangeSetItem> changes = buildDetail.changeSet.items;

        assertThat(changes, is(Matchers.<ChangeSetItem>iterableWithSize(1)));
        assertThat(changes.get(0).msg, is("add some example json from jenkins for testing"));
        assertThat(changes.get(0).comment, is("add some example json from jenkins for testing\nand a second line of magic\n"));
    }

    private String contentFrom(String resourceName) {
        try {
            return IOUtils.toString(JenkinsCommunicatorTest.class.getResourceAsStream(resourceName));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}