package org.netmelody.cieye.witness;

import org.netmelody.cieye.domain.Feature;
import org.netmelody.cieye.domain.TargetGroup;

public interface Witness {

    TargetGroup statusOf(Feature feature);

    long millisecondsUntilNextUpdate(Feature feature);
}
