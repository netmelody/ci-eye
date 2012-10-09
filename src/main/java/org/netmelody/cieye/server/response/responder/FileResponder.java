package org.netmelody.cieye.server.response.responder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.netmelody.cieye.core.logging.LogKeeper;
import org.netmelody.cieye.core.logging.Logbook;
import org.netmelody.cieye.server.response.CiEyeResponder;
import org.netmelody.cieye.server.response.CiEyeResponse;
import org.simpleframework.http.Request;

public final class FileResponder implements CiEyeResponder {

    private static final Logbook LOG = LogKeeper.logbookFor(FileResponder.class);
    private static final ResourceBundle MIME_TYPES = ResourceBundle.getBundle(FileResponder.class.getName());
    
    private final String name;
    private final String extension;

    public FileResponder(String name) {
        this.name = name;
        this.extension = name.substring(name.lastIndexOf('.') + 1);
        LOG.info(name);
    }

    @Override
    public CiEyeResponse respond(Request request) throws IOException {
        InputStream input = null;
        byte[] content = new byte[0];
        try {
            input = getClass().getResourceAsStream(name);
            content = IOUtils.toByteArray(input);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
        return CiEyeResponse.forResource(content, MIME_TYPES.getString(extension));
    }
}

