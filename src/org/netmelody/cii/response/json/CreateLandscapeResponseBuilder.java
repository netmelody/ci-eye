package org.netmelody.cii.response.json;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;

public final class CreateLandscapeResponseBuilder implements JsonResponseBuilder {

    @Override
    public JsonResponse buildResponse() {
        return new JsonResponse(new Landscape("fred"));
    }

}
