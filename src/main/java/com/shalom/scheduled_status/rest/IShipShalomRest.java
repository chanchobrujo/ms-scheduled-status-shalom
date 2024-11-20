package com.shalom.scheduled_status.rest;

import com.shalom.scheduled_status.model.request.ShipShalomRequest;
import com.shalom.scheduled_status.model.response.SearchShalomResponse;

public interface IShipShalomRest {
    SearchShalomResponse getPackage(ShipShalomRequest request);
}
