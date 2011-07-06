package org.netmelody.cieye.server.response;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.Sponsor;

import com.google.common.collect.MapMaker;

public final class Prison {

    private final Map<String, Set<Sponsor>> cells = new MapMaker().makeMap();
    
    public void imprison(Collection<Sponsor> criminals, Landscape crime) {
        cells.put(crime.name(), new HashSet<Sponsor>(criminals));
    }
    
    public void paroleAllPrisonersFor(Landscape crime) {
        cells.remove(crime.name());
    }
    
    public boolean crimeReported(Landscape crime) {
        return cells.containsKey(crime.name());
    }
    
    public Set<Sponsor> prisonersFor(Landscape crime) {
        if (!cells.containsKey(crime.name())) {
            return new HashSet<Sponsor>();
        }
        return new HashSet<Sponsor>(cells.get(crime.name()));
    }
}