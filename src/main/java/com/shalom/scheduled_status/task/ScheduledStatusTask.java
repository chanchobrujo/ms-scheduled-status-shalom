package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
import com.shalom.scheduled_status.service.ISenderMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledStatusTask {
    private final IShipShalomRest shipShalomRest;
    private final IShipStatusRepository shipStatusRepository;
    private final ISenderMessageService senderMessageService;

    @Scheduled(cron = "${application.scheduledTimer}")
    public void reportCurrentTime() {
        var list = this.shipStatusRepository.findByComplete(false);
        list.parallelStream()
                .forEach(shipStatusCurrentSaved -> {
                    var shipStatusResponse = this.shipShalomRest.getPackage(shipStatusCurrentSaved.toRequest());
                    var trackingNumber = shipStatusCurrentSaved.getTrackingNumber();

                    var name = shipStatusResponse.getDestinatario().get("nombre");
                    var subject = "PEDIDO ".concat(trackingNumber);

                    var trackings = shipStatusResponse.getTracking();
                    if (shipStatusResponse.getCompleto()) {
                        var text = "Hola,"+name+" tu pedido ya se encuentra en el local de entrega.";

                        this.senderMessageService.sendMessage(text, subject, "primapp6@gmail.com");
                        shipStatusCurrentSaved.setComplete(true);
                        this.shipStatusRepository.save(shipStatusCurrentSaved);
                    }
                    if (!trackings.isEmpty()) {
                        var currentTracking = shipStatusCurrentSaved.getLastDetectedTracking();
                        var tracking = trackings.get(trackings.size() - 1);

                        if (!tracking.equals(currentTracking)) {
                            var text = "Hola,"+name+"tu pedido esta siendo movilizado, llegara pronto, ultima actualizacion: ".concat(tracking.getDate());

                            this.senderMessageService.sendMessage(text, subject, "primapp6@gmail.com");
                            shipStatusCurrentSaved.setLastDetectedTracking(tracking);
                            this.shipStatusRepository.save(shipStatusCurrentSaved);
                        }
                    }
                });
    }
}
