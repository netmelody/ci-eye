package org.netmelody.cieye.spies.jenkins;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.netmelody.cieye.core.domain.Feature;
import org.netmelody.cieye.core.domain.Target;
import org.netmelody.cieye.core.domain.TargetDigest;
import org.netmelody.cieye.core.domain.TargetDigestGroup;
import org.netmelody.cieye.core.domain.TargetGroup;
import org.netmelody.cieye.core.observation.CiSpy;
import org.netmelody.cieye.core.observation.CommunicationNetwork;
import org.netmelody.cieye.core.observation.KnownOffendersDirectory;
import org.netmelody.cieye.spies.jenkins.jsondomain.Job;
import org.netmelody.cieye.spies.jenkins.jsondomain.View;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class JenkinsSpy implements CiSpy {
    
    private static final Log LOG = LogFactory.getLog(JenkinsSpy.class);
    
    private final JenkinsCommunicator communicator;
    private final JobLaboratory laboratory;

    public JenkinsSpy(String endpoint, CommunicationNetwork network, KnownOffendersDirectory detective) {
        this.communicator = new JenkinsCommunicator(endpoint, network, "ci", "");
        this.laboratory = new JobLaboratory(communicator, detective);
    }

    @Override
    public TargetDigestGroup targetsConstituting(Feature feature) {
        return new TargetDigestGroup(transform(jobsFor(feature), toTargetDigests()));
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        return new TargetGroup(transform(jobsFor(feature), toTargets()));
    }

    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    @Override
    public boolean takeNoteOf(String targetId, String note) {
        final String buildUrl = this.laboratory.lastBadBuildUrlFor(targetId);
        
        if (buildUrl.isEmpty()) {
            return false;
        }
        
        communicator.doJenkinsPost(buildUrl +
                                   "submitDescription?" +
                                   URLEncodedUtils.format(newArrayList(new BasicNameValuePair("description", note)), "UTF-8"));
        return true;
    }
    
    private Collection<Job> jobsFor(final Feature feature) {
        if (!communicator.canSpeakFor(feature)) {
            return newArrayList();
        }
        
        final View viewDigest = find(communicator.views(), withName(feature.name()), null);
        if (null == viewDigest) {
            LOG.error("No view named <" + feature.name() + "> found");
            return newArrayList();
        }
        
        return communicator.jobsFor(viewDigest);
    }

    private Predicate<View> withName(final String featureName) {
        return new Predicate<View>() {
            @Override public boolean apply(View viewDigest) {
                return viewDigest.name.trim().equals(featureName.trim());
            }
        };
    }

    private Function<Job, TargetDigest> toTargetDigests() {
        return new Function<Job, TargetDigest>() {
            @Override public TargetDigest apply(Job jobDigest) {
                return new TargetDigest(jobDigest.url, jobDigest.url, jobDigest.name, jobDigest.status());
            }
        };
    }
    
    private Function<Job, Target> toTargets() {
        return new Function<Job, Target>() {
            @Override public Target apply(Job jobDigest) {
                return laboratory.analyseJob(jobDigest);
            }
        };
    }
}
