package org.netmelody.cieye.core.observation;

import java.text.SimpleDateFormat;

public interface CommunicationNetwork {

    Contact makeContact(SimpleDateFormat dateFormat);
}
