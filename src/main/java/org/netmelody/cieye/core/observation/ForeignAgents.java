package org.netmelody.cieye.core.observation;

import java.net.URL;
import java.util.Set;

import com.google.common.base.Function;

public interface ForeignAgents {

    Set<CallingCard> search();

    public static final class CallingCard {
        public final URL url;
        public CallingCard(URL url) {
            this.url = url;
        }
        
        public static final Function<URL, CallingCard> FROM_URL = new Function<URL, CallingCard>() {
            @Override public CallingCard apply(URL url) { return new CallingCard(url); }
        };
        public static final Function<CallingCard, URL> TO_URL = new Function<CallingCard, URL>() {
            @Override public URL apply(CallingCard input) { return input.url; }
        };
    }

}