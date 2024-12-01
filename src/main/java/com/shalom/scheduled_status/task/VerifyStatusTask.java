package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.document.ShipStatusDocument;
import com.shalom.scheduled_status.model.exception.BusinessException;
import com.shalom.scheduled_status.model.response.SearchShalomResponse;
import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
import com.shalom.scheduled_status.service.ISenderNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Locale;

import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
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
                .filter(c -> isNotEmpty(c.getEmail()))
                .forEach(document ->
                        ofNullable(this.builderMessage(document))
                                .map(document::toNotificationDto)
                                .ifPresent(this.producerService::sendNotification));
        log.info("FIN. ".concat(now().toString()));
    }

    private String builderMessage(ShipStatusDocument document) {
        String message = null;
        try {
            SearchShalomResponse shipStatusResponse = this.shipShalomRest.getPackage(document.toRequest());
            var messageFirstPart = "Hola, "
                    .concat(shipStatusResponse.getDestinatarioName())
                    .concat(", la encomienda que te envia, ")
                    .concat(shipStatusResponse.getRemitenteName());
            if (shipStatusResponse.getCompleto()) {
                message = messageFirstPart.concat(", ya se encuentra en el local de entrega.");
                document.setComplete(true);
                this.shipStatusRepository.save(document);
            }

            var trackings = shipStatusResponse.getTracking();
            if (!trackings.isEmpty()) {
                var currentTracking = document.getLastDetectedTracking();
                var tracking = trackings.get(trackings.size() - 1);

                if (!tracking.equals(currentTracking)) {
                    String dateFormated = parse(tracking.getDate())
                            .atZone(ZoneId.of("America/Lima"))
                            .format(ofPattern("EEEE, dd MMMM, yyyy ', a las' HH 'con' MM 'minutos'", new Locale("es", "ES")));

                    if (isNotEmpty(tracking.getTruck())) {
                        message = messageFirstPart
                                .concat(", esta siendo movilizada en este momento... legara pronto, ")
                                .concat("el ultimo movimiento del camion que lelva tu pedido, fue el ")
                                .concat(dateFormated);
                    } else {
                        message = messageFirstPart
                                .concat(", ya se encuentra en el local de entrega, la hora de llegada fue el ")
                                .concat(dateFormated);
                    }
                    document.setLastDetectedTracking(tracking);
                    this.shipStatusRepository.save(document);
                }
            }
        } catch (BusinessException e) {
            log.error(" Problema al procesar: ".concat(document.toString()));
            log.error(" ".concat(e.getMessage()));
            log.error(" ".concat(e.getLocalizedMessage()));
        }
        return message;
    }
}
