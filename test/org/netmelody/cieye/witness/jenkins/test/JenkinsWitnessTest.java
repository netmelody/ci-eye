package org.netmelody.cieye.witness.jenkins.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import org.junit.Test;
import org.netmelody.cieye.domain.CiServerType;
import org.netmelody.cieye.domain.Feature;
import org.netmelody.cieye.domain.TargetGroup;
import org.netmelody.cieye.persistence.Detective;
import org.netmelody.cieye.witness.jenkins.JenkinsWitness;

public final class JenkinsWitnessTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final JenkinsWitness witness = new JenkinsWitness("http://ci.jenkins-ci.org", new Detective(new File("")));
        
        TargetGroup group = witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        
        assertThat(group, is(notNullValue(TargetGroup.class)));
    }
    
}
