package org.netmelody.cieye.core.observation;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializer;

public final class CodeBook {

    private final SimpleDateFormat dateFormat;
    private final Map<Class<?>, JsonDeserializer<?>> deserialisers;

    public CodeBook() {
        this(new SimpleDateFormat());
    }

    public CodeBook(SimpleDateFormat dateFormat) {
        this(dateFormat, ImmutableMap.<Class<?>, JsonDeserializer<?>>of());
    }

    private CodeBook(SimpleDateFormat dateFormat, ImmutableMap<Class<?>, JsonDeserializer<?>> deserialisers) {
        this.dateFormat = dateFormat;
        this.deserialisers = deserialisers;
    }

    public <T> CodeBook withJsonDeserializerFor(Class<T> type, JsonDeserializer<T> deserialiser) {
        return new CodeBook(this.dateFormat, extend(this.deserialisers, type, deserialiser));
    }

    public SimpleDateFormat dateFormat() {
        return dateFormat;
    }

    public Map<Class<?>, JsonDeserializer<?>> deserialisers() {
        return this.deserialisers;
    }

    private static <X, Y> ImmutableMap<X, Y> extend(Map<X, Y> map, X key, Y value) {
        return ImmutableMap.<X, Y>builder().putAll(map).put(key, value).build();
    }
}