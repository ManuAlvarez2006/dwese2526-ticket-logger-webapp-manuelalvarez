package org.iesalixar.daw2.manuelalvarez.dwese2526_ticket_logger_webapp_manuelalvarez.services;

import java.util.Map;

public interface AppUrlService {
    String buildResetUrl(String rawToken);
    String buildUrl(String path, Map<String, String> queryParams);
}
