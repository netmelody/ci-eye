package org.netmelody.cii.response.json;

import static com.google.common.collect.Lists.newArrayList;

import org.netmelody.cii.domain.Landscape;
import org.netmelody.cii.domain.LandscapeGroup;
import org.netmelody.cii.response.JsonResponse;
import org.netmelody.cii.response.JsonResponseBuilder;

public final class LandscapeListResponseBuilder implements JsonResponseBuilder {

    @Override
    public JsonResponse buildResponse() {
        return new JsonResponse(new LandscapeGroup(newArrayList(new Landscape("Ci-eye Demo"))));
    }

}
