package org.netmelody.cieye.server.configuration;

import java.io.File;

import org.netmelody.cieye.server.PictureFetcher;

public final class Album implements PictureFetcher {

    private final File picturesDirectory;

    public Album(File picturesDirectory) {
        this.picturesDirectory = picturesDirectory;
    }

    @Override
    public File getPictureResource(String name) {
        return new File(picturesDirectory, name);
    }    
}
