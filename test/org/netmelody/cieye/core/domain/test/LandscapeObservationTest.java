package org.netmelody.cieye.core.domain.test;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.netmelody.cieye.core.domain.Percentage.percentageOf;

import org.junit.Test;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.core.domain.RunningBuild;
import org.netmelody.cieye.core.domain.Sponsor;
import org.netmelody.cieye.core.domain.Status;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.server.response.JsonTranslator;

public class LandscapeObservationTest {

    @Test public void
    translatesToAppropriateJsonRepresentationWhenEmpty() {
        final LandscapeObservation observation = new LandscapeObservation();
        
        assertThat(new JsonTranslator().toJson(observation), is("{\"targets\":[]}"));
    }
    
    @Test public void
    translatesToAppropriateJsonRepresentationWithSimpleTargets() {
        final LandscapeObservation observation = new LandscapeObservation(new TargetGroup(newArrayList(new Target("T1ID", "T1URL", "T1", Status.GREEN),
                                                                                                       new Target("T2ID", "T2URL", "T2", Status.BROKEN))));
        
        assertThat(new JsonTranslator().toJson(observation), is("{\"targets\":[" +
                                                                    "{\"id\":\"T1ID\"," +
                                                                     "\"webUrl\":\"T1URL\"," +
                                                                     "\"name\":\"T1\"," + 
                                                                     "\"status\":\"GREEN\"," +
                                                                     "\"lastStartTime\":0," +
                                                                     "\"sponsors\":[]," +
                                                                     "\"builds\":[]}," +
                                                                    "{\"id\":\"T2ID\"," +
                                                                     "\"webUrl\":\"T2URL\"," +
                                                                     "\"name\":\"T2\"," +
                                                                     "\"status\":\"BROKEN\"," +
                                                                     "\"lastStartTime\":0," +
                                                                     "\"sponsors\":[]," +
                                                                     "\"builds\":[]}" +
                                                                "]}"));
    }
    
    @Test public void
    translatesToAppropriateJsonRepresentationWithComplexTarget() {
        final LandscapeObservation observation = new LandscapeObservation(new TargetGroup(newArrayList(
                new Target("T1ID", "T1URL", "T1", Status.GREEN, 123,
                           newArrayList(new RunningBuild(percentageOf(1), Status.GREEN),
                                        new RunningBuild(percentageOf(60), Status.BROKEN)),
                           newHashSet(new Sponsor("S1", "P1"))))));
        
        assertThat(new JsonTranslator().toJson(observation), is("{\"targets\":[" +
                                                                    "{\"id\":\"T1ID\"," +
                                                                     "\"webUrl\":\"T1URL\"," +
                                                                     "\"name\":\"T1\"," + 
                                                                     "\"status\":\"BROKEN\"," +
                                                                     "\"lastStartTime\":123," +
                                                                     "\"sponsors\":[{\"name\":\"S1\",\"picture\":\"P1\"}]," +
                                                                     "\"builds\":[" +
                                                                         "{\"progress\":1,\"status\":\"GREEN\"}," +
                                                                         "{\"progress\":60,\"status\":\"BROKEN\"}" +
                                                                     "]}" +
                                                                "]}"));
    }
    
    @Test public void
    translatesToAppropriateJsonRepresentationWithDohList() {
        final LandscapeObservation observation = new LandscapeObservation().withDoh(newHashSet(new Sponsor("S1", "P1")));
        
        assertThat(new JsonTranslator().toJson(observation), is("{\"targets\":[],\"dohGroup\":[{\"name\":\"S1\",\"picture\":\"P1\"}]}"));
    }
}
