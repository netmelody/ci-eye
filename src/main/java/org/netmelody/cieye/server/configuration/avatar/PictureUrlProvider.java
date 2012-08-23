package org.netmelody.cieye.server.configuration.avatar;

public interface PictureUrlProvider {
    String imageUrlFor(String image);

    String handlesPrefix();
}
