package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.model.dto.NotificationDto;
import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
import com.shalom.scheduled_status.service.sender.notification.ISenderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyStatusTask {
    private final IShipShalomRest shipShalomRest;
    private final ISenderNotificationService producerService;
    private final IShipStatusRepository shipStatusRepository;

    @Scheduled(cron = "${application.verifyStatusTask}", zone = "America/Lima")
    public void verifyStatusTask() {
        System.out.println(" ");
        log.info("VERIFICAR PEDIDOS PENDIENTES. ".concat(now().toString()));
        this.shipStatusRepository
                .findByComplete(false)
                .parallelStream()
                .filter(c -> nonNull(c.getEmail()))
                .forEach(shipStatusCurrentSaved -> {
                    var shipStatusResponse = this.shipShalomRest.getPackage(shipStatusCurrentSaved.toRequest());

                    String emailText = null;
                    String customerName = shipStatusResponse.getDestinatario().get("nombre");
                    String subject = "PEDIDO ".concat(shipStatusCurrentSaved.getTrackingNumber());

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
                        } else log.info("NO HAY ACTUALIZACIONES PARA: {}", subject);
                    }
                    String email = shipStatusCurrentSaved.getEmail();
                    ofNullable(emailText)
                            .map(v -> new NotificationDto(v, email, subject, null))
                            .ifPresent(this.producerService::sendNotification);
                });
        log.info("FIN. ".concat(now().toString()));
    }
}
