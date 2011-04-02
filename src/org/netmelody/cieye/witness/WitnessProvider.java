package org.netmelody.cieye.witness;

import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.observation.CiSpy;

public interface WitnessProvider {

    CiSpy witnessFor(Feature feature);
}
