package org.netmelody.cieye.server.observation;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import static java.lang.Integer.parseInt;

public final class GovernmentWatchdog implements CiEyeNewVersionChecker {

    private final Contact contact;
    
    public static final class TagsHolder {
        private Tags tags;
        
        public TagsHolder() { }
        public TagsHolder(Iterable<Tag> tagNames) { this.tags = new Tags(tagNames); }
        
        public String latest() {
            return tags.latest();
        }
    }
    
    public static final class Tags {
        private final ArrayList<Tag> names;
        public Tags(Iterable<Tag> tagNames) {
            this.names = Lists.newArrayList(tagNames);
            Collections.sort(names);
        }
        public String latest() {
            if (names.isEmpty()) {
                return "";
            }
            return names.get(names.size() - 1).name;
        }
    }
    
    public static final class Tag implements Comparable<Tag> {
        private static final Pattern REGEX = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)([0-9A-Za-z-]*)");

        private final String name;
        private final int[] version;
        private final String specialSuffix;

        public Tag(String name) {
            this.name = name;
            final Matcher matcher = REGEX.matcher(name);
            matcher.matches();
            version = new int[] {parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3))};
            specialSuffix = "".equals(matcher.group(4)) ? "~" : matcher.group(4);
        }

        @Override
        public int compareTo(Tag o) {
            for (int i = 0; i < 3; i++) {
                if (version[i] != o.version[i]) {
                    return version[i] > o.version[i] ? 1 : -1;
                }
            }
            return specialSuffix.compareTo(o.specialSuffix);
        }
    }
    
    public static final class TagsAdapter implements JsonDeserializer<Tags> {
        @Override
        public Tags deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new Tags(transform(json.getAsJsonObject().entrySet(), toTagNames()));
        }

        private Function<Entry<String, JsonElement>, Tag> toTagNames() {
            return new Function<Entry<String, JsonElement>, Tag>() {
                @Override public Tag apply(Entry<String, JsonElement> entry) {
                    return new Tag(entry.getKey());
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
