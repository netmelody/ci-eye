package org.netmelody.cieye.server.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.netmelody.cieye.core.domain.Landscape;
import org.netmelody.cieye.core.domain.Sponsor;

import com.google.common.collect.MapMaker;

public final class Prison {

    private final Map<String, List<Sponsor>> cells = new MapMaker().makeMap();
    
    public void imprison(Collection<Sponsor> criminals, Landscape crime) {
        cells.put(crime.name(), new ArrayList<Sponsor>(criminals));
    }
    
    public void paroleAllPrisonersFor(Landscape crime) {
        cells.remove(crime.name());
    }
    
    public boolean crimeReported(Landscape crime) {
        return cells.containsKey(crime.name());
    }
    
    public List<Sponsor> prisonersFor(Landscape crime) {
        if (!cells.containsKey(crime.name())) {
            return new ArrayList<Sponsor>();
        }
        return new ArrayList<Sponsor>(cells.get(crime.name()));
    }
}