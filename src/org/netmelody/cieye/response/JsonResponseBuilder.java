package org.netmelody.cieye.response;

import org.simpleframework.http.Path;

public interface JsonResponseBuilder {
    JsonResponse buildResponse(Path path, String requestContent);
}
