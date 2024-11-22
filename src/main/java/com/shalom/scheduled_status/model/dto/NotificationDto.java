package com.shalom.scheduled_status.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String text;
    private String email;
    private String subject;
    private String numberPhone;

    public Map<String, String> toBodyMessage() {
        Map<String, String> bodyMessage = new HashMap<>();
        bodyMessage.put("text", this.getText());
        bodyMessage.put("email", this.getEmail());
        bodyMessage.put("subject", this.getSubject());
        bodyMessage.put("numberPhone", this.getNumberPhone());
        return bodyMessage;
    }
}
