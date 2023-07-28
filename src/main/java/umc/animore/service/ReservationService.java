package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.model.*;
import umc.animore.repository.ReservationRepository;
import umc.animore.repository.UserRepository;

import java.time.*;
import java.util.*;

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
    @Autowired
    private UserRepository userRepository;

    private DayOfWeek dayOff1 = null;
    private DayOfWeek dayOff2 = null;


    /** 향후 한달간 예약이 가능한 시간 조회 **/
    public List<LocalDateTime> getAvailableTimesForNextMonth(Long storeId, LocalTime startBookingTime, LocalTime endBookingTime) {
        Store store = storeService.getStoreId(storeId);
        if (store == null) {
            throw new IllegalArgumentException("StoreId is null");
        }

        try {
            dayOff1 = DayOfWeek.valueOf(store.getDayoff1());
        } catch (IllegalArgumentException e) {
            System.out.println("올바르지 않은 dayOff1: " + store.getDayoff1());
        }

        try {
            dayOff2 = DayOfWeek.valueOf(store.getDayoff2());
        } catch (IllegalArgumentException e) {
            System.out.println("올바르지 않은 dayOff2: " + store.getDayoff2());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthLater = now.plusMonths(1);

        List<Reservation> reservations = reservationRepository.findReservationsBetween(now, oneMonthLater);

        List<LocalDateTime> availableTimes = new ArrayList<>();

        Duration timeUntilNextHour = Duration.ofMinutes(120 - now.getMinute());

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

        while (nextHour.isBefore(oneMonthLater.with(startBookingTime))) {
            DayOfWeek dayOfWeek = nextHour.getDayOfWeek();

            if ((dayOff1 != null && dayOfWeek == dayOff1) || (dayOff2 != null && dayOfWeek == dayOff2)) {
                nextHour = nextHour.plusDays(1);
                continue;
            }

            if (nextHour.toLocalTime().isAfter(endBookingTime)) {
                nextHour = nextHour.plusDays(1).with(startBookingTime);
                if (nextHour.isAfter(oneMonthLater.with(startBookingTime))) {
                    break;
                }
                continue;
            }

            int numOfReservations = 0;
            for (Reservation reservation : reservations) {
                if (reservation.getStartTime().isEqual(nextHour)) {
                    numOfReservations++;
                }
            }

            if (numOfReservations < 2) {
                availableTimes.add(nextHour);
            }

            nextHour = nextHour.plusHours(1);
        }

        return availableTimes;
    }

    /** 예약 생성 **/
    public Reservation createReservation(Long user_id, Long storeId, Reservation.DogSize dogSize, Reservation.CutStyle cutStyle, Reservation.BathStyle bathStyle) {
        User user = userService.getUserId(user_id);
        if(user == null) {
            // 예외처리
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다.");
        }

        Pet petInfo = petService.findByUserId(user_id);
        if (petInfo == null) {
            throw new IllegalStateException("유저의 해당 반려동물 정보를 찾을 수 없습니다.");
        }

        Store store = storeService.getStoreId(storeId);
        if (store == null) {
            throw new IllegalArgumentException("해당 매장을 찾을 수 없습니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setUsername(user.getUsername());
        reservation.setUser_phone(user.getPhone());
        reservation.setAddress(user.getAddress());
        reservation.setPet_name(petInfo.getPetName());
        reservation.setPet_type(petInfo.getPetType());
        reservation.setPet_gender(petInfo.getPetGender());
        reservation.setDogSize(dogSize);
        reservation.setBathStyle(bathStyle);
        reservation.setCutStyle(cutStyle);
        reservation.setStore(store);

        return reservationRepository.save(reservation);
    }

    /** 예약상세 3 **/
    public Reservation insertBookingtime(Long reservationId, LocalDateTime startTime) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("reservationId is null");
        }

        // 예약 기간 계산 (1시간) 수정할 수 있게 해줘야 함.
        LocalDateTime endTime = startTime.plusHours(1);
//
//        // 겹치는 예약 확인 로직: 등록된 예약 중 겹치는 예약 반환
        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservations(startTime, endTime);
//
//        // 겹치는 예약 수 확인 후 2개 이상이면 예약 불가 (수정 가능하게)
        if (overlappingReservations.size() >= 2) {
            throw new IllegalArgumentException("해당 시간은 예약이 풀입니다.");
        }
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        if (reservation.getStartTime() == null) {
            throw new IllegalArgumentException("reservation time 설정 오류");
        }

        System.out.println("== 날짜 시간 insert 완료");

        return reservationRepository.save(reservation);
    }


    /** 예약 수정 **/
    public Reservation updateReservation(Long reservationId, LocalDateTime startTime) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("해당 예약이 존재하지 않습니다.");
        }

        LocalDateTime endTime = startTime.plusHours(1);

        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservations(startTime, endTime);
        overlappingReservations.remove(reservation);

        // 동일한 예약 시간은 수정 불가능하도록 함
        if (reservation.getStartTime().isEqual(startTime)) {
            return reservation;
        }

        if (!overlappingReservations.isEmpty()) {
            throw new IllegalArgumentException("해당 시간은 예약이 풀입니다.");
        }

        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(1));

        System.out.println("=====예약 수정 완료=====");

        return reservationRepository.save(reservation);

    }


    /** 예약 삭제 **/
    public void deleteReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("해당 예약이 존재하지 않습니다.");
        }

        reservationRepository.deleteById(reservationId);

        System.out.println("예약 삭제 완료");
    }



    // 관리페이지 (업체-예약 관리1)
    /** 업체-예약관리1 **/
    public List<Reservation> getMonthlyReservationsByStore(Store store, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return reservationRepository.findByStoreAndStartTimeBetweenOrderByStartTime(store, startOfMonth, endOfMonth);
    }

    /** 업체-예약관리2 **/
    public Page<Reservation> getRequest(int confirmed, Pageable pageable) {

        if (confirmed != 0 && confirmed != 1) {
            throw new IllegalArgumentException("confirmed값이 0또는 1이 아닙니다.");
        }

        return reservationRepository.findByConfirmed(confirmed, pageable);

    }

    /** 예약 상세정보 **/
    public Reservation getRequestById(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
        if (optionalReservation.isPresent()) {
            return optionalReservation.get();
        } else {
            throw new RuntimeException("해당 예약이 존재하지 않습니다");
        }
    }

    /** 업체 - 예약승인 **/
    public Reservation confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("해당 예약이 존재하지 않습니다.");
        }

        reservation.setConfirmed(1);

        return reservationRepository.save(reservation);
    }

    /** 업체 - 예약반려 **/
    public Reservation rejectReservation(Long reservationId, String causeReject) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("해당 예약이 존재하지 않습니다.");
        }
        reservation.setConfirmed(0);
        reservation.setCause(causeReject);

        return reservationRepository.save(reservation);
    }

    /** 유저 예약내역 **/
    public Page<Reservation> getReservationlist(Long user_id, Pageable pageable) {
        User user = userRepository.findById(user_id);

        if (user_id == null) {
            throw new IllegalArgumentException("user_id is null");
        }

        return reservationRepository.findByUserId(user_id, pageable);

    }
}