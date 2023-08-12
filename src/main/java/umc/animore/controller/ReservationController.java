package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseResponse;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.Reservation;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.model.reservation.ReservationRequest;
import umc.animore.service.EmailService;
import umc.animore.service.ReservationService;
import umc.animore.service.StoreService;
import umc.animore.service.UserService;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static umc.animore.config.exception.BaseResponseStatus.*;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;


    // 향후 한달간 예약 가능한 시간 조회
    @ResponseBody
    @GetMapping("/booking/Calendar")
    public BaseResponse<?> getAvailableTimesForNextMonth(@RequestBody ReservationRequest reservationRequest) {

        Store store = storeService.findStoreId(reservationRequest.getStoreId());

        Map<String, Object> response = new LinkedHashMap<>();

        if (store == null) {
            return new BaseResponse<>(NO_MATCHING_STORE);

        } else {
            store.setDayoff1(store.getDayoff1().toUpperCase());
            store.setDayoff2(store.getDayoff2().toUpperCase());

            List<LocalDateTime> availableTimes = reservationService.getAvailableTimesForNextMonth(reservationRequest.getStoreId(), LocalTime.of(store.getOpen(), 0), LocalTime.of(store.getClose() - 1, 0));
            response.put("result", availableTimes);

            if (availableTimes.isEmpty()) {
                return new BaseResponse<>(NO_TIME_AVAILABLE);

            }
        }

        return new BaseResponse<>(response);
    }


    // 예약 생성
    @ResponseBody
    @PostMapping("/booking/create")
    public BaseResponse<Map<String, Object>> createReservation(@RequestBody ReservationRequest reservationRequest) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationRequest.getStoreId() == null) {
            return new BaseResponse<>(DATABASE_ERROR);
        }
        try {
            Reservation reservation = reservationService.createReservation(user.getId(), reservationRequest.getStoreId(), reservationRequest.getDogSize(), reservationRequest.getCutStyle(), reservationRequest.getBathStyle());

            Map<String, Object> reservationResult = new LinkedHashMap<>();
            reservationResult.put("reservationId", reservation.getReservationId());
            reservationResult.put("username", reservation.getUsername());
            reservationResult.put("address", reservation.getAddress());
            reservationResult.put("phone", reservation.getUser_phone());
            reservationResult.put("pet_name", reservation.getPet_name());
            reservationResult.put("pet_gender", reservation.getPet_gender());
            reservationResult.put("pet_type", reservation.getPet_type());
            reservationResult.put("dogSize", reservation.getDogSize());
            reservationResult.put("cutStyle", reservation.getCutStyle());
            reservationResult.put("bathStyle", reservation.getBathStyle());

            return new BaseResponse<>(reservationResult);
        } catch (Exception e) {
            return new BaseResponse<>(RESPONSE_ERROR);
        }

    }

    // 예약상세 3
    @ResponseBody
    @PostMapping("/booking/time/{reservationId}")
    public BaseResponse<Map<String, Object>> insertBookTime(@PathVariable("reservationId") Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        List<Reservation> nullReservations = reservationService.findReservationWithNullStartTime(user.getId());
        if (nullReservations.isEmpty()) {
            return new BaseResponse<>(NO_MATCHING_STORE);
        }

        Optional<Reservation> mostRecentNullReservation = nullReservations.stream()
                .max(Comparator.comparing(Reservation::getCreate_at));

        if (!mostRecentNullReservation.isPresent()) {
            return new BaseResponse<>(NOT_FOUND_RECENT_BOOKING);
        }

        Reservation nullReservation = mostRecentNullReservation.get();


        Map<String, Object> reservationResult = new LinkedHashMap<>();

        try {
            Reservation inserTIme = reservationService.insertBookingtime(reservationId, startTime);

            reservationResult.put("reservationId", inserTIme.getReservationId());
            reservationResult.put("username", inserTIme.getUsername());
            reservationResult.put("address", inserTIme.getAddress());
            reservationResult.put("phone", inserTIme.getUser_phone());
            reservationResult.put("pet_gender", inserTIme.getPet_gender());
            reservationResult.put("pet_type", inserTIme.getPet_type());
            reservationResult.put("dogSize", inserTIme.getDogSize());
            reservationResult.put("cutStyle", inserTIme.getCutStyle());
            reservationResult.put("bathStyle", inserTIme.getBathStyle());
            reservationResult.put("startTime", inserTIme.getStartTime());

        } catch (Exception e) {
            return new BaseResponse<>(RESPONSE_ERROR);

        }

        return new BaseResponse<>(reservationResult);
    }

    // 예약상세 저장내용 불러오기
    @ResponseBody
    @GetMapping("/userInfo")
    public BaseResponse<Map<String, Object>> getUserInfo() {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();


        try {
            Map<String, Object> userinfoMap = userService.getUserInfo(user.getId());
            if (userinfoMap.isEmpty()) {
                return new BaseResponse<>(RESPONSE_ERROR);

            }
            return new BaseResponse<>(userinfoMap);
        } catch (Exception e) {
            return new BaseResponse<>(DATABASE_ERROR);

        }

    }


    // 예약 시간 수정
    @ResponseBody
    @PutMapping("/my/booking/update/time/{reservationId}")
    public BaseResponse<Map<String, Object>> updateReservation(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        Reservation reservation = reservationService.findbyUserId(reservationId, user.getId());

        if (!user.getId().equals(reservation.getUser().getId())) {
            return new BaseResponse<>(NOT_MATCHED_USER);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        try {
            Reservation updatedReservation = reservationService.updateReservation(reservationId, startTime);

        } catch (Exception e) {
            return new BaseResponse<>(RESERVAION_MODIFY_ERROR);
        }

        return new BaseResponse<>(SUCCESS);
    }

    // 예약 요청사항 수정
    @ResponseBody
    @PutMapping("/my/booking/update/request/{reservationId}")
    public BaseResponse<?> modifyRequest(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        Reservation reservation = reservationService.findbyUserId(reservationId, user.getId());

        if (!user.getId().equals(reservation.getUser().getId())) {
            return new BaseResponse<>(NOT_MATCHED_USER);
        }

        if (reservationRequest.getCutStyle() == null || reservationRequest.getBathStyle() == null || reservationRequest.getDogSize() == null) {
            return new BaseResponse<>(EMPTY_REQUEST_VALUE);
        }


        try {
            reservationService.modifyRequest(reservationId, reservationRequest.getDogSize(), reservationRequest.getCutStyle(), reservationRequest.getBathStyle());
        } catch (IllegalArgumentException e) {
            return new BaseResponse<>(RESERVAION_MODIFY_ERROR);
        }
        return new BaseResponse<>(SUCCESS);
    }

    // 예약 삭제
    @ResponseBody
    @DeleteMapping("/my/booking/delete/{reservationId}")
    public BaseResponse<?> deleteReservation(@PathVariable Long reservationId) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        Reservation reservation = reservationService.findbyUserId(reservationId, user.getId());

        if (!user.getId().equals(reservation.getUser().getId())) {
            return new BaseResponse<>(NOT_MATCHED_USER);
        }

        try {
            reservationService.deleteReservation(reservationId);

        } catch (IllegalArgumentException e) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);

        }
        return new BaseResponse<>(SUCCESS);
    }

    // 업체 - 예약관리1
    @ResponseBody
    @GetMapping("/manage/bookings")
    public BaseResponse<?> ReservationStoreMonth(@RequestParam int year, @RequestParam int month) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = principalDetails.getUser();
        Store store = user.getStore();

        if (store == null) {
            return new BaseResponse<>(NO_MATCHING_STORE);
        }

        try {
            List<Reservation> reservations = reservationService.getMonthlyReservationsByStore(store, year, month);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

            List<Map<String, Object>> result = new ArrayList<>();
            for (Reservation reservation : reservations) {
                Map<String, Object> reservationData = new LinkedHashMap<>();

                LocalDateTime dateTime = reservation.getStartTime();
                reservationData.put("time", timeFormatter.format(dateTime));
                reservationData.put("petName", reservation.getPet_name());
                reservationData.put("confirmed", reservation.getConfirmed());

                result.add(reservationData);
            }

            return new BaseResponse<>(result);

        } catch (Exception e) {
            return new BaseResponse<>(DATABASE_ERROR);
        }

    }

    // 업체 - 예약관리2 예약요청
    @ResponseBody
    @GetMapping("/manage/bookings/requests")
    public BaseResponse<?> reservationRequestsList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        try {
            Page<Reservation> reservationPage = reservationService.getRequest(user.getStore().getStoreId(), false, pageable);
            List<Reservation> reservationList = reservationPage.getContent();

            List<Map<String, Object>> result = new ArrayList<>();
            for (Reservation r : reservationList) {
                Map<String, Object> reservationMap = new LinkedHashMap<>();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                String dateString = r.getStartTime().format(formatter);
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MM.dd.HH:mm"));
                reservationMap.put("startTime", formattedDate);
                reservationMap.put("petName", r.getPet_name());
                reservationMap.put("username", r.getUsername());
                reservationMap.put("phone", r.getUser_phone());
                result.add(reservationMap);
            }

            return new BaseResponse<>(result);
        } catch (Exception e) {
            return new BaseResponse<>(SERVER_ERROR);
        }

    }


    // 업체 - 예약관리4 예약완료
    @ResponseBody
    @GetMapping("/manage/bookings/confirmed")
    public BaseResponse<?> reservationConfirmedList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        try {
            Page<Reservation> reservationPage = reservationService.getRequest(user.getStore().getStoreId(), true, pageable);
            List<Reservation> reservationList = reservationPage.getContent();

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Reservation r : reservationList) {
                Map<String, Object> reservationMap = new LinkedHashMap<>();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                String dateString = r.getStartTime().format(formatter);
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MM.dd.HH:mm"));
                reservationMap.put("startTime", formattedDate);
                reservationMap.put("petName", r.getPet_name());
                reservationMap.put("username", r.getUsername());
                reservationMap.put("phone", r.getUser_phone());
                resultList.add(reservationMap);
            }

            return new BaseResponse<>(resultList);
        } catch (Exception e) {
            return new BaseResponse<>(SERVER_ERROR);
        }

    }

    // 예약 상세보기
    @ResponseBody
    @GetMapping("/booking/details/{reservationId}")
    public BaseResponse<?> reservationRequest(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.getRequestById(reservationId);


        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

       try {
           Map<String, Object> reservationMap = new HashMap<>();
           reservationMap.put("petName", reservation.getPet_name());
           reservationMap.put("petType", reservation.getPet_type());
           reservationMap.put("petGender", reservation.getPet_gender());
           reservationMap.put("username", reservation.getUsername());
           reservationMap.put("phone", reservation.getUser_phone());
           reservationMap.put("address", reservation.getAddress());
           reservationMap.put("dogSize", reservation.getDogSize());
           reservationMap.put("cutStyle", reservation.getCutStyle());
           reservationMap.put("bathStyle", reservation.getBathStyle());

           return new BaseResponse<>(reservationMap);

       } catch (Exception e) {
           return new BaseResponse<>(DATABASE_ERROR);
       }
    }

    // 업체 - 예약승인
    @ResponseBody
    @GetMapping("/manage/bookings/confirm/{reservationId}")
    public BaseResponse<?> confirmedReservation(@PathVariable Long reservationId) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        Store store = reservationService.findByStore(reservationId);

        if (user.getStore().getStoreId() != store.getStoreId()) {
            return new BaseResponse<>(INVALID_REQUEST_INFO);
        }

        reservationService.confirmReservation(reservationId);


        return new BaseResponse<>(SUCCESS);
    }

    // 업체 - 예약반려
    @ResponseBody
    @GetMapping("/manage/bookings/reject/{reservationId}")
    public BaseResponse<?> rejectReservation(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        if (reservationId == null) {
            return new BaseResponse<>(NOT_FOUND_RESERVATION);
        }

        Store store = reservationService.findByStore(reservationId);

        if (user.getStore().getStoreId() != store.getStoreId()) {
            return new BaseResponse<>(INVALID_REQUEST_INFO);
        }

        reservationService.rejectReservation(reservationId, reservationRequest.getCause());

        return new BaseResponse<>(SUCCESS);
    }

    // 유저 - 예약내역
    @ResponseBody
    @GetMapping("/my/booking/visit")
    public BaseResponse<?> reservationList(@PageableDefault(size = 6, page = 0, sort = "userId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        try {
            Page<Reservation> reservationlist = reservationService.getReservationlist(user.getId(), pageable);
            List<Reservation> reservations = reservationlist.getContent();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Reservation i : reservations) {
                Map<String,Object> reservationMap = new HashMap<>();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                LocalDateTime dateTime = i.getStartTime();
                String formattedDate;
                if (dateTime != null) {
                    formattedDate = dateTime.format(formatter.ofPattern("MM.dd.HH:mm"));
                } else {
                    formattedDate = "N/A";
                }

                reservationMap.put("startTime", formattedDate);
                reservationMap.put("storeName", i.getStore().getStoreName());
                reservationMap.put("storeLocation", i.getStore().getStoreLocation());
                reservationMap.put("storeNumber", i.getStore().getStoreNumber());

                result.add(reservationMap);
            }



            return new BaseResponse<>(result);
        } catch (Exception e) {
            return new BaseResponse<>(SERVER_ERROR);
        }
    }

    // 예약 알림 메일 테스트
    @GetMapping("/sendEmail")
    public void sendEmail(String emailto) {
        String to = emailto;
        String subject = "테스트 메일 제목2";
        String text = "테스트 메일 내용2";

        emailService.sendEmail(to, subject, text);
    }
}