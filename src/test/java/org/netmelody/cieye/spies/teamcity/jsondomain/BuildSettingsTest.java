package org.netmelody.cieye.spies.teamcity.jsondomain;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BuildSettingsTest {

    @Test
    public void isDisabledWhenAllowExternalStatusIsFalse() {
        BuildSettings buildSettings = createBuildSettingsWithAllowExternalStatus("false");

        assertThat(buildSettings.externalStatusDisabled(), equalTo(true));
    }

    @Test
    public void isNotDisabledWhenAllowExternalStatusIsTrue() {
        BuildSettings buildSettings = createBuildSettingsWithAllowExternalStatus("true");

        assertThat(buildSettings.externalStatusDisabled(), equalTo(false));
    }

    @Test
    public void isNotDisabledWhenAllowExternalStatusIsNotPresent() {
        BuildSettings buildSettings = new BuildSettings();
        buildSettings.property = newArrayList();

        assertThat(buildSettings.externalStatusDisabled(), equalTo(false));
    }

    private BuildSettings createBuildSettingsWithAllowExternalStatus(String allowExternalStatus) {
        BuildSettings buildSettings = new BuildSettings();
        final BuildSettingsProperty property = new BuildSettingsProperty();
        property.name = "allowExternalStatus";
        property.value = allowExternalStatus;
        buildSettings.property = newArrayList(property);
        return buildSettings;
    }
}
