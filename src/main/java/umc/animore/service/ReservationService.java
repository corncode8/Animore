package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.model.*;
import umc.animore.repository.ReservationRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private UserService userService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PetService petService;
    @Autowired
    private StoreService storeService;


    // 향후 한달간 예약이 가능한 시간 조회
    public List<LocalDateTime> getAvailableTimesForNextMonth(LocalTime startBookingTime, LocalTime endBookingTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthLater = now.plusMonths(1);

        List<Reservation> reservations = reservationRepository.findReservationsBetween(now, oneMonthLater);

        List<LocalDateTime> availableTimes = new ArrayList<>();

        Duration timeUntilNextHour = Duration.ofMinutes(60 - now.getMinute());

        LocalDateTime nextHour = now
                .plus(timeUntilNextHour)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        if (nextHour.toLocalTime().isBefore(startBookingTime)) {
            nextHour = nextHour.with(startBookingTime);
        } else if (nextHour.toLocalTime().isAfter(endBookingTime)) {
            nextHour = nextHour.plusDays(1).with(startBookingTime);
        }

        for (LocalDateTime time = nextHour; time.isBefore(oneMonthLater); time = time.plusHours(1)) {
            if (time.toLocalTime().isAfter(endBookingTime)) {
                time = time.plusDays(1).with(startBookingTime);
                if (time.isAfter(oneMonthLater)) {
                    break;
                }
            }

            int numOfReservations = 0;
            for (Reservation reservation : reservations) {
                if (reservation.getStartTime().isEqual(time)) {
                    numOfReservations++;
                }
            }

            if (numOfReservations < 2) {
                availableTimes.add(time);
            }
        }

        return availableTimes;
    }

    // 예약 생성
    public Reservation createReservation(Long user_idx, LocalDateTime startTime,String request,Long storeId, MultipartFile imageFile) {
        User user = userService.getUserId(user_idx);
        if(user == null) {
            // 예외처리
        }

        Pet petInfo = petService.getPetInfo(user);
        if (petInfo == null) {
            throw new IllegalStateException("Pet information not found for the user.");
        }

        Store store = storeService.getStoreId(storeId);

//        if (timeSlot == null) {
//            throw new IllegalStateException("Cannot find a valid TimeSlot for the requested time.");
//        }

        // 예약 기간 계산 (2시간) 수정할 수 있게 해줘야 함.
        LocalDateTime endTime = startTime.plusHours(2);

        // 겹치는 예약 확인 로직: 등록된 예약 중 겹치는 예약 반환
        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservations(startTime, endTime);

        // 겹치는 예약 수 확인 후 2개 이상이면 예약 불가 (수정 가능하게)
        if (overlappingReservations.size() >= 2) {
            throw new IllegalArgumentException("해당 시간은 예약이 풀입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStartTime(startTime);
        reservation.setUsername(user.getUsername());
        reservation.setUser_phone(user.getPhone());
        reservation.setAddress(user.getAddress());
        reservation.setPet_name(petInfo.getPetName());
        reservation.setPet_type(petInfo.getPetType());
        reservation.setPet_gender(petInfo.getPetGender());
        reservation.setRequest(request);
        reservation.setStore(store);
        reservation.setEndTime(endTime);

        if (imageFile != null && !imageFile.isEmpty()) {
            Image image = new Image();
            // 이미지 처리  (저장, 경로 설정)
            reservation.getImages().add(image);
            image.setReservation(reservation);
        }
        System.out.println("예약 생성 로직 완료");
        return reservationRepository.save(reservation);
    }



    // 예약 수정


    // 예약 삭제


    // 예약 확인
}
