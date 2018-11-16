package org.netmelody.cieye.server.configuration.test;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;

import org.junit.Test;
import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.server.configuration.PluginDirectory;
import org.netmelody.cieye.server.configuration.ServiceLoadingRecordedForeignAgencies;

public final class ServiceLoadingRecordedForeignAgenciesTest {

    private final ServiceLoadingRecordedForeignAgencies foreignAgencies = new ServiceLoadingRecordedForeignAgencies(new PluginDirectory(new File(".")));

    @Test public void
    loadsObeservationModules() {
        assertThat(foreignAgencies.agencyFor(new CiServerType("DEMO")), is(not(nullValue())));
        assertThat(foreignAgencies.agencyFor(new CiServerType("HUDSON")), is(not(nullValue())));
        assertThat(foreignAgencies.agencyFor(new CiServerType("JENKINS")), is(not(nullValue())));
        assertThat(foreignAgencies.agencyFor(new CiServerType("TEAMCITY")), is(not(nullValue())));
    }

    @Test public void
    handlesRequestsForAnUnknownAgency() {
        try {
            foreignAgencies.agencyFor(new CiServerType("SAUSAGE"));
            fail("Expected an IllegalStateException");
        }
        catch (Exception e) {
            assertThat(e.getMessage(), containsString("No CI Observation Module for SAUSAGE"));
        }
    }

    @Test(expected=NullPointerException.class) public void
    handlesRequestsForANullAgency() {
        foreignAgencies.agencyFor(null);
    }
}
