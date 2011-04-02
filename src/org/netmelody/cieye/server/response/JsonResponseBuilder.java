package org.netmelody.cieye.server.response;

import org.simpleframework.http.Path;

public interface JsonResponseBuilder {
    JsonResponse buildResponse(Path path, String requestContent);
}
