package org.netmelody.cieye.server.observation;

import java.util.HashMap;
import java.util.Map;

import org.netmelody.cieye.core.domain.CiServerType;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.server.configuration.State;
import org.netmelody.cieye.server.observation.protocol.JsonRestRequesterBuilder;
import org.netmelody.cieye.spies.demo.DemoModeWitness;
import org.netmelody.cieye.spies.jenkins.JenkinsWitness;
import org.netmelody.cieye.spies.teamcity.TeamCityWitness;

public final class DefaultWitnessProvider implements WitnessProvider {

    private final Map<String, CiSpy> witnesses = new HashMap<String, CiSpy>();
    private final CommunicationNetwork network = new JsonRestRequesterBuilder();
    private final KnownOffendersDirectory detective;
    
    public DefaultWitnessProvider(State state) {
        detective = state.detective();
    }
    
    @Override
    public CiSpy witnessFor(Feature feature) {
        if (witnesses.containsKey(feature.endpoint())) {
            return witnesses.get(feature.endpoint());
        }
        
        CiSpy witness = new DemoModeWitness(detective);
        if (CiServerType.JENKINS.equals(feature.type())) {
            witness = new BufferedWitness(new JenkinsWitness(feature.endpoint(), network, detective));
        }
        else if (CiServerType.TEAMCITY.equals(feature.type())) {
            witness = new BufferedWitness(new TeamCityWitness(feature.endpoint(), network, detective));
        }
        witnesses.put(feature.endpoint(), witness);
        return witness;
    }

}
