package org.netmelody.cieye.server.configuration.avatar;

class LocalImage implements PictureUrlProvider {
    @Override
    public String imageUrlFor(String image) {
        return "/pictures/" + image;
    }

    @Override
    public String handlesPrefix() {
        return "";
    }
}
