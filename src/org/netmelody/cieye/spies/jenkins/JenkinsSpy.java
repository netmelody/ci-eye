package org.netmelody.cieye.spies.jenkins;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.LandscapeObservation;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;

import com.google.common.base.Predicate;

public final class JenkinsSpy implements CiSpy {
    
    private static final Log LOG = LogFactory.getLog(JenkinsSpy.class);
    
    private final JenkinsCommunicator communicator;
    private final ViewAnalyser viewAnalsyer;

    public JenkinsSpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.communicator = new JenkinsCommunicator(endpoint, network, "ci", "");
        this.viewAnalsyer = new ViewAnalyser(communicator, detective);
    }

    @Override
    public LandscapeObservation statusOf(final Feature feature) {
        if (!communicator.canSpeakFor(feature)) {
            return new LandscapeObservation();
        }
        
        final View viewDigest = find(communicator.views(), withName(feature.name()), null);
        if (null == viewDigest) {
            LOG.error("No view named <" + feature.name() + "> found");
            return new LandscapeObservation();
        }
        
        return new LandscapeObservation(viewAnalsyer.analyse(viewDigest));
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        final String buildUrl = this.viewAnalsyer.lastBadBuildUrlFor(targetId);
        
        if (buildUrl.isEmpty()) {
            return false;
        }
        
        communicator.doJenkinsPost(buildUrl +
                                   "submitDescription?" +
                                   URLEncodedUtils.format(newArrayList(new BasicNameValuePair("description", note)), "UTF-8"));
        return true;
    }
    
    private Predicate<View> withName(final String featureName) {
        return new Predicate<View>() {
            @Override public boolean apply(View viewDigest) {
                return viewDigest.name.trim().equals(featureName.trim());
            }
        };
    }
}
