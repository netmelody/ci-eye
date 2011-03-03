package org.netmelody.cii.witness.jenkins;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

import java.util.Collection;

import org.netmelody.cii.domain.Feature;
import org.netmelody.cii.domain.TargetGroup;
import org.netmelody.cii.persistence.Detective;
import org.netmelody.cii.witness.Witness;
import org.netmelody.cii.witness.jenkins.jsondomain.Server;
import org.netmelody.cii.witness.jenkins.jsondomain.Users;
import org.netmelody.cii.witness.jenkins.jsondomain.UserDetail;
import org.netmelody.cii.witness.jenkins.jsondomain.View;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public final class JenkinsWitness implements Witness {
    
    private final JenkinsCommunicator communicator;
    private final ViewAnalyser viewAnalsyer;

    public JenkinsWitness(String endpoint, Detective detective) {
        this.communicator = new JenkinsCommunicator(endpoint);
        this.viewAnalsyer = new ViewAnalyser(communicator, detective);
    }

    @Override
    public TargetGroup statusOf(final Feature feature) {
        if (!communicator.endpoint().equals(feature.endpoint())) {
            return new TargetGroup();
        }
        
        final View viewDigest = filter(views(), new Predicate<View>() {
            @Override public boolean apply(View viewDigest) {
                return viewDigest.name.startsWith(feature.name());
            }
        }).iterator().next();
        
        return new TargetGroup(viewAnalsyer.analyse(viewDigest));
    }
    
    @Override
    public long millisecondsUntilNextUpdate(Feature feature) {
        return 0L;
    }
    
    public Collection<String> users() {
        final Users detail = communicator.makeJenkinsRestCallWithSuffix("people", Users.class);
        return transform(detail.users, new Function<UserDetail, String>() {
            @Override public String apply(UserDetail userDetail) { return userDetail.user.fullName; }
        });
    }
    
    private Collection<View> views() {
        return communicator.makeJenkinsRestCallWithSuffix("", Server.class).views;
    }

//    private Computer agentDetails(String agentName) {
//        return makeJenkinsRestCall(endpoint + "/computer/" + agentName, Computer.class);
//    }
    
//    private void changeDescription(String jobName, String buildNumber, String newDescription) {
//        "/submitDescription?Submit=Submit&description=" + encodeURI(change.desc) + "&json={\"description\":\"" + change.desc + "\"}";
//    }
    

}
