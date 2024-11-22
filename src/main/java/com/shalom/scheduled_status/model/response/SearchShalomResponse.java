package com.shalom.scheduled_status.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shalom.scheduled_status.model.dto.TrackingDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@JsonInclude(NON_EMPTY)
public class SearchShalomResponse {
    private Boolean completo = false;

    private String email;
    private String _origen;
    private String _destino;
    private String trackingNumber;

    private Long ose_id;
    private String code;
    private Boolean aereo;
    private Boolean reparto;
    private String contenido;
    private String fecha;
    private Map<String, String> remitente = new HashMap<>();
    private Map<String, String> destinatario = new HashMap<>();
    private List<TrackingDto> tracking = new ArrayList<>();
}
