package org.netmelody.cieye.core.domain.test;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.LandscapeGroup;
import org.netmelody.cieye.server.response.JsonTranslator;

public class LandscapeGroupTest {

    @Test public void
    translatesToAppropriateJsonRepresentationWhenEmpty() {
        final LandscapeGroup group = new LandscapeGroup();
        
        assertThat(new JsonTranslator().toJson(group), is("{\"landscapes\":[]}"));
    }
    
    @Test public void
    translatesToAppropriateJsonRepresentation() {
        final LandscapeGroup group = new LandscapeGroup(newArrayList(new Landscape("L1", new Feature("F11", "E11", new CiServerType("T11")),
                                                                                         new Feature("F12", "E12", new CiServerType("T12"))),
                                                                     new Landscape("L2", new Feature("F21", "E21", new CiServerType("T21")),
                                                                                         new Feature("F22", "E22", new CiServerType("T22")))));
        
        assertThat(new JsonTranslator().toJson(group), is("{\"landscapes\":[" +
                                                              "{\"name\":\"L1\"}," +
                                                              "{\"name\":\"L2\"}" +
                                                          "]}"));
    }
}
