package org.netmelody.cieye.spies.teamcity;

import java.text.SimpleDateFormat;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.core.observation.ObservationAgency;

import com.google.common.base.Function;

public final class TeamCityObservationAgency implements ObservationAgency {

    @Override
    public CiSpy provideSpyFor(Feature feature, CommunicationNetwork network, KnownOffendersDirectory directory) {
        final CodeBook codeBook = new CodeBook(new SimpleDateFormat("yyyyMMdd'T'HHmmssZ"))
                                      .withCredentials(feature.username(), feature.password())
                                      .withRawContentMunger(new Function<String, String>() {
                                          @Override public String apply(String input) {
                                              return input.replace("\"@", "\"");
                                          }
                                      });

        return new TeamCitySpy(feature.endpoint(), directory, network.makeContact(codeBook));
    }

    @Override
    public boolean canProvideSpyFor(CiServerType type) {
        return "TEAMCITY".equals(type.name());
    }
}