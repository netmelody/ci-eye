package org.netmelody.cieye.server.configuration.avatar;


public final class Robohash implements PictureUrlProvider {

    private final static String ROBOHASH_URL = "http://robohash.org/";

    @Override
    public String imageUrlFor(String picture) {
        return ROBOHASH_URL + picture.toLowerCase().trim() + ".jpg";
    }

    @Override
    public String handlesPrefix() {
        return "robohash";
    }

}
