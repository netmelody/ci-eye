package org.netmelody.cieye.witness;

import org.netmelody.cieye.core.domain.Feature;

public interface WitnessProvider {

    Witness witnessFor(Feature feature);
}
