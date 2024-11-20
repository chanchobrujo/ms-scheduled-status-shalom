package com.shalom.scheduled_status.rest;

import com.shalom.scheduled_status.model.exception.BusinessException;
import com.shalom.scheduled_status.model.request.ShipShalomRequest;
import com.shalom.scheduled_status.model.response.SearchShalomResponse;
import com.shalom.scheduled_status.properties.ApisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.util.Optional.of;
import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipShalomRest implements IShipShalomRest {
    private final RestTemplate clientRest;
    private final ApisProperties apisProperties;

    @Override
    public SearchShalomResponse getPackage(ShipShalomRequest request) {
        var uri = this.apisProperties._shipShalom();
        var requestEntity = new HttpEntity<>(request);
        return of(this.clientRest.exchange(uri, POST, requestEntity, SearchShalomResponse.class))
                .map(HttpEntity::getBody)
                .orElseThrow(()-> new BusinessException("Error al consumir servicio."));
    }
}
