package org.netmelody.cii.witness;

import java.util.HashMap;
import java.util.Map;

import org.netmelody.cii.domain.CiServerType;
import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.witness.jenkins.JenkinsWitness;
import org.netmelody.cii.witness.teamcity.TeamCityWitness;

public final class DefaultWitnessProvider implements WitnessProvider {

    private final Map<String, Witness> witnesses = new HashMap<String, Witness>();
    
    @Override
    public Witness witnessFor(Feature feature) {
        if (witnesses.containsKey(feature.endpoint())) {
            return witnesses.get(feature.endpoint());
        }
        
        Witness witness = new DummyWitness();
        if (CiServerType.JENKINS.equals(feature.type())) {
            witness = new BufferedWitness(new JenkinsWitness(feature.endpoint()));
        }
        else if (CiServerType.TEAMCITY.equals(feature.type())) {
            witness = new BufferedWitness(new TeamCityWitness(feature.endpoint()));
        }
        witnesses.put(feature.endpoint(), witness);
        return witness;
    }

}
