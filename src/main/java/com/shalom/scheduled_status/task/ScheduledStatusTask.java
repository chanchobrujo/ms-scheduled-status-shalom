package com.shalom.scheduled_status.task;

import com.shalom.scheduled_status.repository.IShipStatusRepository;
import com.shalom.scheduled_status.rest.IShipShalomRest;
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

    @Scheduled(cron = "${application.scheduledTimer}")
    public void reportCurrentTime() {
        var list = this.shipStatusRepository.findByComplete(false);
        list.parallelStream()
                .forEach(shipStatusCurrentSaved -> {
                    var shipStatusResponse = this.shipShalomRest.getPackage(shipStatusCurrentSaved.toRequest());
                    var trackings = shipStatusResponse.getTracking();
                    if (shipStatusResponse.getCompleto()) {
                        log.info("enviar email.");
                        shipStatusCurrentSaved.setComplete(true);
                        this.shipStatusRepository.save(shipStatusCurrentSaved);
                    }
                    if (!trackings.isEmpty()) {
                        var currentTracking = shipStatusCurrentSaved.getLastDetectedTracking();
                        var tracking = trackings.get(trackings.size() - 1);

                        if (!tracking.equals(currentTracking)) {
                            log.info("enviar email.");
                            shipStatusCurrentSaved.setLastDetectedTracking(tracking);
                            this.shipStatusRepository.save(shipStatusCurrentSaved);
                        }
                    }
                });
    }
}
