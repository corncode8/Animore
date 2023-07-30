package umc.animore.model.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import umc.animore.model.Reservation;
import umc.animore.service.ReservationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Component
public class ReservationCleanTask {

    private static final long INTERVAL = 1000 * 60 * 30;
    private static final long MAX_INACTIVE_TIME = Duration.ofHours(1).toMillis();
    @Autowired
    private ReservationService reservationService;

    @Scheduled(fixedDelay = INTERVAL)
    public void cleanunfinished() {
        List<Reservation> unfinishedReservations =  reservationService.findUnfinishedReservations();

        for (Reservation reservation : unfinishedReservations) {
            if (isInactiveForTooLong(reservation)) {
                reservationService.deleteReservation(reservation.getReservationId());
            }
        }
    }

    private boolean isInactiveForTooLong(Reservation reservation) {
        LocalDateTime creationTime = reservation.getCreate_at().toLocalDateTime();
        return Duration.between(creationTime, LocalDateTime.now()).toMillis() > MAX_INACTIVE_TIME;
    }


}
