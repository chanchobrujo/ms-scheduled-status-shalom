package com.shalom.scheduled_status.service;

public interface ISenderMessageService {
    void sendMessage(String text, String subject, String email);
}
