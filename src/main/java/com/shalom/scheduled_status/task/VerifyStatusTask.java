package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.document.ShipStatusDocument;
import com.shalom.scheduled_status.model.exception.BusinessException;
import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
import com.shalom.scheduled_status.service.ISenderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.time.LocalDateTime.now;
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
        log.info("VERIFICAR PEDIDOS PENDIENTES. ".concat(now().toString()));
        this.shipStatusRepository
                .findByComplete(false)
                .parallelStream()
                .filter(c -> isNotEmpty(c.getEmail()))
                .forEach(document ->
                        ofNullable(this.builderMessage(document))
                                .map(document::toNotificationDto)
                                .ifPresent(this.producerService::sendNotification));
        log.info("FIN. ".concat(now().toString()));
    }

    private String builderMessage(ShipStatusDocument document) {
        String emailText = null;
        try {
            var shipStatusResponse = this.shipShalomRest.getPackage(document.toRequest());

            if (shipStatusResponse.getCompleto()) {
                emailText = "Hola, ".concat(shipStatusResponse.getCustomerName())
                        .concat(" tu pedido ya se encuentra en el local de entrega.");
                document.setComplete(true);
                this.shipStatusRepository.save(document);
            }

            var trackings = shipStatusResponse.getTracking();
            if (!trackings.isEmpty()) {
                var currentTracking = document.getLastDetectedTracking();
                var tracking = trackings.get(trackings.size() - 1);

                if (!tracking.equals(currentTracking)) {
                    emailText = "Hola, ".concat(shipStatusResponse.getCustomerName())
                            .concat(" tu pedido esta siendo movilizado, llegara pronto, ultima actualización: ")
                            .concat(tracking.getDate());
                    document.setLastDetectedTracking(tracking);
                    this.shipStatusRepository.save(document);
                }
            }
        } catch (BusinessException e) {
            log.error(" Problema al procesar: ".concat(document.toString()));
            log.error(" ".concat(e.getMessage()));
            log.error(" ".concat(e.getLocalizedMessage()));
        }
        return emailText;
    }
}
