package org.netmelody.cieye.server.configuration.avatar;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Maps.uniqueIndex;

public final class PictureUrlRegistry {
    
    private final Map<String, PictureUrlProvider> providers = uniqueIndex(ImmutableList.of(new Gravatar(),
                                                                                           new Robohash(),
                                                                                           new SimpleHttp(),
                                                                                           new LocalImage()),
                                                                          PictureUrlProvider.TO_PREFIX);

    private String urlFor(String prefix, String suffix) {
        return providers.get(prefix).imageUrlFor(suffix);
    }

    public String getPictureUrl(final String image) {
        final String[] parts = image.split(":", 2);
        return (parts.length == 1) ? urlFor("", image) : urlFor(parts[0], parts[1]);
    }
}
