package org.netmelody.cieye.witness;

import java.util.HashMap;
import java.util.Map;

import org.netmelody.cieye.domain.CiServerType;
import org.netmelody.cieye.domain.Feature;
import org.netmelody.cieye.persistence.Detective;
import org.netmelody.cieye.persistence.State;
import org.netmelody.cieye.witness.jenkins.JenkinsWitness;
import org.netmelody.cieye.witness.teamcity.TeamCityWitness;

public final class DefaultWitnessProvider implements WitnessProvider {

    private final Map<String, Witness> witnesses = new HashMap<String, Witness>();
    private final Detective detective;
    
    public DefaultWitnessProvider(State state) {
        detective = state.detective();
    }
    
    @Override
    public Witness witnessFor(Feature feature) {
        if (witnesses.containsKey(feature.endpoint())) {
            return witnesses.get(feature.endpoint());
        }
        
        Witness witness = new DemoModeWitness(detective);
        if (CiServerType.JENKINS.equals(feature.type())) {
            witness = new BufferedWitness(new JenkinsWitness(feature.endpoint(), detective));
        }
        else if (CiServerType.TEAMCITY.equals(feature.type())) {
            witness = new BufferedWitness(new TeamCityWitness(feature.endpoint(), detective));
        }
        witnesses.put(feature.endpoint(), witness);
        return witness;
    }

}
