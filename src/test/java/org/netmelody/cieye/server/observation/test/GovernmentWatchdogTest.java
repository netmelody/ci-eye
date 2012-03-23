package org.netmelody.cieye.server.observation.test;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.netmelody.cieye.core.observation.CodeBook;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.Contact;
import org.netmelody.cieye.server.observation.GovernmentWatchdog;
import org.netmelody.cieye.server.observation.GovernmentWatchdog.Tag;
import org.netmelody.cieye.server.observation.GovernmentWatchdog.Tags;
import org.netmelody.cieye.server.observation.GovernmentWatchdog.TagsAdapter;
import org.netmelody.cieye.server.observation.GovernmentWatchdog.TagsHolder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class GovernmentWatchdogTest {
    
    @Test public void
    deserialisesJsonString() {
        final String json = "{\"tags\":{\"0.0.2\":\"9f09249bb60faf1ffd8d19d48bcd3706552e3ef7\"," +
                                       "\"0.0.3\":\"b161cbbc5bd891467b91ed3f3fe5d77516b8c1bd\"," +
                                       "\"0.0.4\":\"ed6afa9cace6b203c351cfbed83683574a353fe6\"}}";
        
        final Gson gson = new GsonBuilder().registerTypeAdapter(Tags.class, new TagsAdapter()).create();
        final TagsHolder holder = gson.fromJson(json, TagsHolder.class);
        
        assertThat(holder.latest(), is("0.0.4"));
    }
    
    @Test public void
    processesJsonResponse() {
        final Mockery context = new Mockery();
        final CommunicationNetwork network = context.mock(CommunicationNetwork.class);
        final Contact contact = context.mock(Contact.class);
        
        context.checking(new Expectations() {{
            allowing(network).makeContact(with(any(CodeBook.class))); will(returnValue(contact));
                
            oneOf(contact).makeJsonRestCall("http://github.com/api/v2/json/repos/show/netmelody/ci-eye/tags", TagsHolder.class);
                will(returnValue(new TagsHolder(newArrayList(new Tag("0.0.1"), new Tag("0.0.2"), new Tag("0.0.3"), new Tag("0.0.4")))));
        }});
        
        final GovernmentWatchdog watchdog = new GovernmentWatchdog(network);
        final String latestVersion = watchdog.getLatestVersion();
        
        context.assertIsSatisfied();
        assertThat(latestVersion, equalTo("0.0.4"));
    }

    @Test public void
    ranksTagsNumerically() {
        final String json = "{\"tags\":{\"0.0.2\":\"\",\"0.0.11\":\"\"}}";
        
        final Gson gson = new GsonBuilder().registerTypeAdapter(Tags.class, new TagsAdapter()).create();
        final TagsHolder holder = gson.fromJson(json, TagsHolder.class);
        
        assertThat(holder.latest(), is("0.0.11"));
    }
    
    @Test public void
    ranksBetaTagsLower() {
        final String json = "{\"tags\":{\"0.0.1beta1\":\"\",\"0.0.1\":\"\"}}";
        
        final Gson gson = new GsonBuilder().registerTypeAdapter(Tags.class, new TagsAdapter()).create();
        final TagsHolder holder = gson.fromJson(json, TagsHolder.class);
        
        assertThat(holder.latest(), is("0.0.1"));
    }
}
