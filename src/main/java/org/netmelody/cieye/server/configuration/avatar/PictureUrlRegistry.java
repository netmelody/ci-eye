package org.netmelody.cieye.server.configuration.avatar;

import java.util.ArrayList;
import java.util.List;

public final class PictureUrlRegistry {
    private final List<PictureUrlProvider> providers;
    private final LocalImage defaultProvider;

    public PictureUrlRegistry() {
        this.providers = new ArrayList<PictureUrlProvider>();
        providers.add(new Gravatar());
        providers.add(new Robohash());
        providers.add(new SimpleHttp());
        defaultProvider = new LocalImage();
        providers.add(defaultProvider);
    }

    public PictureUrlProvider providerFor(String prefix) {
        for (PictureUrlProvider provider : providers) {
            if (provider.handlesPrefix().equals(prefix)) {
                return provider;
            }
        }
        return defaultProvider;
    }

    public String getPictureUrl(final String image) {
        String[] imageParts = image.split(":");
        PictureUrlProvider provider;
        if (imageParts.length == 1) {
            provider = defaultProvider;
            return provider.imageUrlFor(image);
        } else {
            provider = providerFor(imageParts[0]);
            return provider.imageUrlFor(imageParts[1]);
        }
    }
}
