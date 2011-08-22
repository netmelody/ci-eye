package org.netmelody.cieye.server.observation;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import static com.google.common.collect.Iterables.transform;

public final class GovernmentWatchdog implements CiEyeNewVersionChecker {

    private final Contact contact;
    
    public static final class TagsHolder {
        private Tags tags;
        
        public TagsHolder() { }
        public TagsHolder(Iterable<String> tagNames) { this.tags = new Tags(tagNames); }
        
        public String latest() {
            return tags.latest();
        }
    }
    
    public static final class Tags {
        private final ArrayList<String> names;
        public Tags(Iterable<String> tagNames) {
            this.names = Lists.newArrayList(tagNames);
            Collections.sort(names);
        }
        public String latest() {
            if (names.isEmpty()) {
                return "";
            }
            return names.get(names.size() - 1);
        }
    }
    
    public static final class TagsAdapter implements JsonDeserializer<Tags> {
        @Override
        public Tags deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new Tags(transform(json.getAsJsonObject().entrySet(), toTagNames()));
        }

        private Function<Entry<String, JsonElement>, String> toTagNames() {
            return new Function<Entry<String, JsonElement>, String>() {
                @Override public String apply(Entry<String, JsonElement> entry) {
                    return entry.getKey();
                }
            };
        }
    }
    
    public GovernmentWatchdog(CommunicationNetwork network) {
        contact = network.makeContact(new SimpleDateFormat(), Tags.class, new TagsAdapter());
    }

    @Override
    public String getLatestVersion() {
        TagsHolder tags = contact.makeJsonRestCall("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", TagsHolder.class);
        
        return tags.latest();
    }
}
