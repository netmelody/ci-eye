package org.netmelody.cii.witness;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;

public interface Witness {

    TargetGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);
}
