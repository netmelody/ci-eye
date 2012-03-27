package org.netmelody.cieye.core.observation;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import static com.google.common.base.Functions.compose;

public final class CodeBook {

    private final String username;
    private final String password;
    private final SimpleDateFormat dateFormat;
    private final ImmutableMap<Class<?>, JsonDeserializer<?>> deserialisers;
    private final Function<String, String> munger;

    public CodeBook() {
        this(new SimpleDateFormat());
    }

    public CodeBook(SimpleDateFormat dateFormat) {
        this("", "", dateFormat, Functions.<String>identity(), ImmutableMap.<Class<?>, JsonDeserializer<?>>of());
    }

    private CodeBook(String username, String password, SimpleDateFormat dateFormat, Function<String, String> munger, ImmutableMap<Class<?>, JsonDeserializer<?>> deserialisers) {
        this.username = username;
        this.password = password;
        this.dateFormat = dateFormat;
        this.deserialisers = deserialisers;
        this.munger = munger;
    }

    public CodeBook withCredentials(String name, String pass) {
        return new CodeBook(name, pass, this.dateFormat, this.munger, this.deserialisers);
    }

    public CodeBook withRawContentMunger(Function<String, String> munger) {
        return new CodeBook(this.username, this.password, this.dateFormat, compose(munger, this.munger), this.deserialisers);
    }

    public <T> CodeBook withJsonDeserializerFor(Class<T> type, JsonDeserializer<T> deserialiser) {
        return new CodeBook(this.username, this.password, this.dateFormat, this.munger, extend(this.deserialisers, type, deserialiser));
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public Function<String, String> contentMunger() {
        return munger;
    }

    private static <X, Y> ImmutableMap<X, Y> extend(Map<X, Y> map, X key, Y value) {
        return ImmutableMap.<X, Y>builder().putAll(map).put(key, value).build();
    }

    public Gson decoder() {
        final GsonBuilder builder = new GsonBuilder().setDateFormat(dateFormat.toPattern());
        
        for (Entry<Class<?>, JsonDeserializer<?>> entry : this.deserialisers.entrySet()) {
            builder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        return builder.create();
    }
}