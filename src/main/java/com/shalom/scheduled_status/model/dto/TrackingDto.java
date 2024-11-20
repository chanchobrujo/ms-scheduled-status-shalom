package com.shalom.scheduled_status.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDto {
    private String truck;
    private String date;

    public LocalDateTime _date() {
        return LocalDateTime.parse(this.date);
    }
}
