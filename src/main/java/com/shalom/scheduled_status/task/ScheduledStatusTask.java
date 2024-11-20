package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
import com.shalom.scheduled_status.service.ISenderMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledStatusTask {
    private final IShipShalomRest shipShalomRest;
    private final IShipStatusRepository shipStatusRepository;
    private final ISenderMessageService senderMessageService;

    @Scheduled(cron = "${application.scheduledTimer}")
    public void reportCurrentTime() {
        log.info("VERIFICAR PEDIDOS PENDIENTES. ".concat(now().minusHours(5).toString()));
        this.shipStatusRepository
                .findByComplete(false)
                .parallelStream()
                .forEach(shipStatusCurrentSaved -> {
                    var shipStatusResponse = this.shipShalomRest.getPackage(shipStatusCurrentSaved.toRequest());

                    String emailText = null;
                    String customerEmail = "primapp6@gmail.com";
                    String customerName = shipStatusResponse.getDestinatario().get("nombre");
                    String emailSubject = "PEDIDO ".concat(shipStatusCurrentSaved.getTrackingNumber());

                    if (shipStatusResponse.getCompleto()) {
                        emailText = "Hola, ".concat(customerName).concat(" tu pedido ya se encuentra en el local de entrega.");
                        shipStatusCurrentSaved.setComplete(true);
                        this.shipStatusRepository.save(shipStatusCurrentSaved);
                    }

                    var trackings = shipStatusResponse.getTracking();
                    if (!trackings.isEmpty()) {
                        var currentTracking = shipStatusCurrentSaved.getLastDetectedTracking();
                        var tracking = trackings.get(trackings.size() - 1);

                        if (!tracking.equals(currentTracking)) {
                            emailText = "Hola, ".concat(customerName).concat(" tu pedido esta siendo movilizado, llegara pronto, ultima actualización: ").concat(tracking.getDate());
                            shipStatusCurrentSaved.setLastDetectedTracking(tracking);
                            this.shipStatusRepository.save(shipStatusCurrentSaved);
                        } else log.info("NO HAY ACTUALIZACIONES PARA: " + emailSubject);
                    }
                    ofNullable(emailText).ifPresent(t -> this.senderMessageService.sendMessage(t, emailSubject, customerEmail));
                });
        log.info("FIN. ".concat(now().minusHours(5).toString()));
    }
}
