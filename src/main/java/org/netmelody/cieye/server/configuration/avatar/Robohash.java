package org.netmelody.cieye.server.configuration.avatar;

import java.text.MessageFormat;

final class Robohash implements PictureUrlProvider {

    private final static String ROBOHASH_URL = "http://robohash.org/";

    @Override
    public String imageUrlFor(String picture) {
        return MessageFormat.format("{0}{1}.png", ROBOHASH_URL, picture.toLowerCase().trim());
    }

    @Override
    public String handlesPrefix() {
        return "robohash";
    }

}
