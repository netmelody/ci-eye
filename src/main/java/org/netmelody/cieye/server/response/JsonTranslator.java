package org.netmelody.cieye.server.response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Optional;
import org.netmelody.cieye.core.domain.Percentage;
import org.netmelody.cieye.core.domain.TargetId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonTranslator {

    private final Gson gson;

    public JsonTranslator() {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Percentage.class, new JsonSerializer<Percentage>() {
            @Override
            public JsonElement serialize(Percentage src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.value());
            }
        });
        builder.registerTypeAdapter(TargetId.class, new JsonSerializer<TargetId>() {
            @Override
            public JsonElement serialize(TargetId src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.id());
            }
        });
        builder.registerTypeAdapter(Optional.class, new JsonSerializer<Optional<?>>() {
            @Override
            public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
                if (src.isPresent()) {
                    Type innerType = ((ParameterizedType) typeOfSrc).getActualTypeArguments()[0];
                    return context.serialize(src.get(), innerType);
                } else {
                    return context.serialize(null);
                }
            }
        });

        gson = builder.create();
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }
}
