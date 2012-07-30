package org.netmelody.cieye.server.observation;

import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.CiEyeNewVersionChecker;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
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
        
        public TagsHolder() { this(ImmutableList.<Tag>of()); }
        public TagsHolder(Iterable<Tag> tagNames) { this.tags = new Tags(tagNames); }
        
        public String latest() {
            return tags.latest();
        }
    }

    public static final class Tags {
        private final ArrayList<Tag> names;
        public Tags(Iterable<Tag> tagNames) {
            this.names = Lists.newArrayList(tagNames);
            Collections.sort(names, Tag.COMPARATOR);
        }
        public String latest() {
            if (names.isEmpty()) {
                return "";
            }
            return names.get(names.size() - 1).name;
        }
    }

    public static final class Tag {
        private static final Pattern REGEX = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)([0-9A-Za-z-]*)");

        private static final Comparator<Tag> COMPARATOR = new Comparator<Tag>() {
            @Override
            public int compare(Tag o1, Tag o2) {
                for (int i = 0; i < 3; i++) {
                    if (o1.version[i] != o2.version[i]) {
                        return o1.version[i] > o2.version[i] ? 1 : -1;
                    }
                }
                return o1.specialSuffix.compareTo(o2.specialSuffix);
            }
        };
        
        private final String name;
        private final int[] version;
        private final String specialSuffix;

        public Tag(String name) {
            this.name = (name == null) ? "" : name;
            final Matcher matcher = REGEX.matcher(this.name);
            matcher.matches();
            version = new int[] {parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3))};
            specialSuffix = "".equals(matcher.group(4)) ? "~" : matcher.group(4);
        }
    }

    public static final class TagsHolderAdapter implements JsonDeserializer<TagsHolder> {
        @Override
        public TagsHolder deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new TagsHolder(transform(json.getAsJsonArray(), toTagNames()));
        }
        
        private Function<JsonElement, Tag> toTagNames() {
            return new Function<JsonElement, Tag>() {
                @Override public Tag apply(JsonElement entry) {
                    return new Tag(entry.getAsJsonObject().get("name").getAsString());
                }
            };
        }
    }

    public GovernmentWatchdog(CommunicationNetwork network) {
        contact = network.makeContact(new CodeBook().withJsonDeserializerFor(TagsHolder.class, new TagsHolderAdapter()));
    }

    @Override
    public String getLatestVersion() {
        try {
            InetAddress.getAllByName("api.github.com");
        }
        catch (UnknownHostException firewalled) {
            return "";
        }
        final TagsHolder tags = contact.makeJsonRestCall("https://api.github.com/repos/netmelody/ci-eye/tags", TagsHolder.class);
        return tags.latest();
    }
}
