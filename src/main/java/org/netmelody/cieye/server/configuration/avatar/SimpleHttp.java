package org.netmelody.cieye.server.configuration.avatar;


public final class SimpleHttp implements PictureUrlProvider {

    @Override
    public String imageUrlFor(String picture) {
        return "http:" + picture;
    }

    @Override
    public String handlesPrefix() {
        return "http";
    }

}
