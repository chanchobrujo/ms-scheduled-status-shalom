package com.shalom.scheduled_status.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipShalomRequest {
    private String code;
    private String number;
}
