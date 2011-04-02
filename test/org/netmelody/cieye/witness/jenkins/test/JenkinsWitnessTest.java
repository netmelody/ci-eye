package org.netmelody.cieye.witness.jenkins.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.server.configuration.RecordedKnownOffenders;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequesterBuilder;
import org.netmelody.cieye.spies.jenkins.JenkinsWitness;

public final class JenkinsWitnessTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final JenkinsWitness witness = new JenkinsWitness("http://ci.jenkins-ci.org", new JsonRestRequesterBuilder(), new RecordedKnownOffenders(new File("")));
        
        TargetGroup group = witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        
        assertThat(group, is(notNullValue(TargetGroup.class)));
    }
    
}
