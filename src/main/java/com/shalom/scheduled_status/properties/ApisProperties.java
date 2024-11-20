package com.shalom.scheduled_status.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@ToString
@Configuration
public class ApisProperties {
    @Value("${spring.data.mongodb.host}")
    private String value;
    @Value("${apis.ship-shalom}")
    private String shipShalom;

    public String _shipShalom() {
        return this.getShipShalom().replace("{VPS_IP}", this.getValue());
    }
}
