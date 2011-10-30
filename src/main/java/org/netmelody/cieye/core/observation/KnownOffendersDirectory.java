package org.netmelody.cieye.core.observation;

import java.util.Set;

import org.netmelody.cieye.core.domain.Sponsor;

public interface KnownOffendersDirectory {

    Set<Sponsor> search(String fingerprint);

}