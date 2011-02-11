package org.netmelody.cii.witness;

import org.netmelody.cii.domain.Feature;

public interface WitnessProvider {

    Witness witnessFor(Feature feature);
}
