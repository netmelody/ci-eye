package org.netmelody.cieye.spies.teamcity.jsondomain;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.List;

public class BuildSettings {
    public List<BuildSettingsProperty> property;

    public boolean externalStatusDisabled() {
        Optional<BuildSettingsProperty> allowExternalStatus = Iterables.tryFind(property, new Predicate<BuildSettingsProperty>() {
            @Override
            public boolean apply(BuildSettingsProperty input) {
                return "allowExternalStatus".equals(input.name);
            }
        });

        if (allowExternalStatus.isPresent()) {
            return Boolean.parseBoolean(allowExternalStatus.get().value);
        } else {
            return false;
        }
    }
}
