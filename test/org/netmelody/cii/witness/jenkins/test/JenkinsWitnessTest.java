package org.netmelody.cii.witness.jenkins.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;

import org.junit.Test;
import org.netmelody.cii.domain.CiServerType;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.jenkins.JenkinsWitness;

public class JenkinsWitnessTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        final JenkinsWitness witness = new JenkinsWitness("http://ci.jenkins-ci.org", new Detective(new File("")));
        
        TargetGroup group = witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        witness.statusOf(new Feature("Jenkins core", "http://ci.jenkins-ci.org", CiServerType.JENKINS));
        
        assertThat(group, is(notNullValue(TargetGroup.class)));
    }
    
}
