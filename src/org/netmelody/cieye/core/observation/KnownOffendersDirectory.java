package org.netmelody.cieye.core.observation;

import java.util.List;

import org.netmelody.cieye.core.domain.Sponsor;

public interface KnownOffendersDirectory {

    List<Sponsor> search(String fingerprint);

}