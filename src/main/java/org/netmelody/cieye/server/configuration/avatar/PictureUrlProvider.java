package org.netmelody.cieye.server.configuration.avatar;

import com.google.common.base.Function;

public interface PictureUrlProvider {
    
    String imageUrlFor(String image);

    String handlesPrefix();

    public static Function<PictureUrlProvider, String> TO_PREFIX = new Function<PictureUrlProvider, String>() {
        @Override public String apply(PictureUrlProvider provider) {
            return provider.handlesPrefix();
        }
    };
}
