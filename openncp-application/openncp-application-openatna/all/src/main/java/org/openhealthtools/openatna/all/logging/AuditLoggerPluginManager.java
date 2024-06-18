package org.openhealthtools.openatna.all.logging;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface AuditLoggerPluginManager {

    void start() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException,  NoSuchMethodException,  IllegalArgumentException, InvocationTargetException;

    void destroy();

    void handleAuditEvent(HttpServletRequest request, Map<String, String> queryParameters, List<Long> messageEntityIds);
}
