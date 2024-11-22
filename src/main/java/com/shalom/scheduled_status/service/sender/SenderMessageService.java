package com.shalom.scheduled_status.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class SenderMessageService {

    @Autowired
    @Qualifier(value = "SenderMailService")
    private ISenderService senderMailService;

    @Autowired
    @Qualifier(value = "SenderMessageTextService")
    private ISenderService senderMessageTextService;

    public void send(Map<String, String> message) {
        this.senderMailService.send(message);
        this.senderMessageTextService.send(message);
    }
}
