package org.netmelody.cii.witness.jenkins.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.netmelody.cii.domain.Target;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.jenkins.JenkinsCommunicator;
import org.netmelody.cii.witness.jenkins.ViewAnalyser;
import org.netmelody.cii.witness.jenkins.jsondomain.View;


public final class ViewAnalyserTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        ViewAnalyser analyser = new ViewAnalyser(new JenkinsCommunicator("http://ci.jenkins-ci.org"), new Detective(new File("")));

        View viewDigest = new View();
        viewDigest.url = "http://ci.jenkins-ci.org/view/Jenkins%20core";
        Collection<Target> result1 = analyser.analyse(viewDigest);
        new ArrayList<Target>(result1);
        
        Collection<Target> result2 = analyser.analyse(viewDigest);
        result2.size();
        new ArrayList<Target>(result2);
        
        //assertThat(group, is(notNullValue(TargetGroup.class)));
    }
    
}
