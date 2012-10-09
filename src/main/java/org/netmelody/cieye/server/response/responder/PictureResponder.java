package org.netmelody.cieye.server.response.responder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.PictureFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.simpleframework.http.Request;

public final class PictureResponder implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(PictureResponder.class);
    private static final ResourceBundle MIME_TYPES = ResourceBundle.getBundle(FileResponder.class.getName());
    
    private final String name;
    private final String extension;
    private final PictureFetcher pictureFetcher;

    public PictureResponder(PictureFetcher pictureFetcher, String name) {
        this.pictureFetcher = pictureFetcher;
        this.name = name;
        this.extension = name.substring(name.lastIndexOf('.') + 1);
        LOG.info(name);
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        FileInputStream picture = null;
        byte[] content = new byte[0];
        try {
            final File file = pictureFetcher.getPictureResource(name);
            picture = new FileInputStream(file);
            content = IOUtils.toByteArray(picture);
            return CiEyeResponse.forResource(content, MIME_TYPES.getString(extension)).lastModified(file.lastModified());
        }
        finally {
            IOUtils.closeQuietly(picture);
        }
    }
}

