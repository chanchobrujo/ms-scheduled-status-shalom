package com.shalom.scheduled_status.service.sender;

import java.util.Map;

public interface ISenderService {
    void send(Map<String, String> message);
}
