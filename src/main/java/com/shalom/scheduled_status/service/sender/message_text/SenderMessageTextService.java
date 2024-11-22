package com.shalom.scheduled_status.service.sender.message_text;

import com.shalom.scheduled_status.service.sender.ISenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("SenderMessageTextService")
public class SenderMessageTextService implements ISenderService {
    @Override
    public void send(Map<String, String> message) {
        log.info("mensaje de texto xd");
    }
}
