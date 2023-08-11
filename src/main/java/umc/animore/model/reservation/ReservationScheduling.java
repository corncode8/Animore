package umc.animore.model.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import umc.animore.model.Reservation;
import umc.animore.repository.ReservationRepository;
import umc.animore.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class ReservationScheduling {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(fixedDelay = 60 * 30 * 1000)
    public void checkReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusMinutes(120);
        List<Reservation> reservations = reservationRepository.findByStartTimeBetween(now, end);

        for (Reservation r: reservations) {
            emailService.sendEmail(r.getUser().getEmail(), "Animore 예약 알림", "예약 알림 : " + r.getStartTime() + " 에 시작합니다.");
        }
    }
}
