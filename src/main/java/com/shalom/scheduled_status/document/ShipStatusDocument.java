package com.shalom.scheduled_status.document;

import com.shalom.scheduled_status.model.dto.TrackingDto;
import com.shalom.scheduled_status.model.request.ShipShalomRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipStatusDocument {
    @Id
    private String trackingNumber;
    private String code;
    private String email;
    private boolean complete = false;
    private TrackingDto lastDetectedTracking;
    private List<TrackingDto> tracking = new ArrayList<>();

    public ShipShalomRequest toRequest() {
        return new ShipShalomRequest(this.getCode(), this.getTrackingNumber());
    }
}
