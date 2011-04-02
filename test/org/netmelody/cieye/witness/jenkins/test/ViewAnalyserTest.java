package org.netmelody.cieye.witness.jenkins.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.persistence.RecordedKnownOffenders;
import org.netmelody.cieye.spies.jenkins.JenkinsCommunicator;
import org.netmelody.cieye.spies.jenkins.ViewAnalyser;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;
import org.netmelody.cieye.witness.protocol.JsonRestRequesterBuilder;


public final class ViewAnalyserTest {

    @Test public void
    canPullFromTheJenkinsLiveInstance() {
        ViewAnalyser analyser = new ViewAnalyser(new JenkinsCommunicator("http://ci.jenkins-ci.org", new JsonRestRequesterBuilder(), "", ""), new RecordedKnownOffenders(new File("")));

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
