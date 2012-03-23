package org.netmelody.cieye.core.observation;

import java.text.SimpleDateFormat;

public interface CommunicationNetwork {

    Contact makeContact(CodeBook codeBook);
    Contact makeContact(SimpleDateFormat dateFormat);
    Contact makeContact(SimpleDateFormat dateFormat, Class<?> type, Object typeAdapter);
}
