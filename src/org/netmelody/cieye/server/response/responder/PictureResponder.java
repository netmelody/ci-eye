package org.netmelody.cieye.server.response.responder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netmelody.cieye.server.PictureFetcher;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.simpleframework.http.Response;

public final class PictureResponder implements CiEyeResponder {

    private static final Log LOG = LogFactory.getLog(PictureResponder.class);
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
    public void writeTo(Response response) throws IOException {
        FileInputStream picture = null;
        OutputStream body = null;
        try {
            body = response.getOutputStream();
            final File file = pictureFetcher.getPictureResource(name);
            picture = new FileInputStream(file);
            response.set("Content-Type", contentTypeOf(extension));
            response.setDate("Last-Modified", file.lastModified());
            IOUtils.copy(picture, body);
        }
        finally {
            IOUtils.closeQuietly(picture);
            IOUtils.closeQuietly(body);
        }
    }

    private static String contentTypeOf(String extension) {
        return MIME_TYPES.getString(extension);
    }
}

