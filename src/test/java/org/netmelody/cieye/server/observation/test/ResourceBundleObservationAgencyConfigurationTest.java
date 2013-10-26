package org.netmelody.cieye.server.observation.test;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.server.observation.ObservationAgencyConfiguration;
import org.netmelody.cieye.server.observation.ResourceBundleObservationAgencyConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;

public final class ResourceBundleObservationAgencyConfigurationTest {

    private final ObservationAgencyConfiguration config = new ResourceBundleObservationAgencyConfiguration();
    
    @Test public void
    loadsObeservationModules() {
        assertThat(config.agencyFor(new CiServerType("DEMO")), is(not(nullValue())));
        assertThat(config.agencyFor(new CiServerType("HUDSON")), is(not(nullValue())));
        assertThat(config.agencyFor(new CiServerType("JENKINS")), is(not(nullValue())));
        assertThat(config.agencyFor(new CiServerType("TEAMCITY")), is(not(nullValue())));
    }
    
    @Test public void
    handlesRequestsForAnUnknownAgency() {
        try {
            config.agencyFor(new CiServerType("SAUSAGE"));
            fail("Expected an IllegalStateException");
        }
        catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("No CI Observation Module for SAUSAGE"));
        }
    }

    @Test(expected=NullPointerException.class) public void
    handlesRequestsForANullAgency() {
        config.agencyFor(null);
    }
}
