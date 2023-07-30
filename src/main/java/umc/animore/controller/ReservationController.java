package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.model.Reservation;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.model.reservation.ReservationRequest;
import umc.animore.service.ReservationService;
import umc.animore.service.StoreService;
import umc.animore.service.UserService;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;


    // 향후 한달간 예약 가능한 시간 조회
    @ResponseBody
    @GetMapping("/booking/Calendar")
    public ResponseEntity<Map<String, Object>> getAvailableTimesForNextMonth() {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Store store = storeService.findStoreId(user.getStore().getStoreId());

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (store == null) {
            response.put("isSuccess", false);
            response.put("code", 2001); // Store ID를 찾을 수 없는 경우
            response.put("message", "Store ID를 찾을 수 없습니다.");
            status = HttpStatus.NOT_FOUND;
        } else {
            store.setDayoff1(store.getDayoff1().toUpperCase());
            store.setDayoff2(store.getDayoff2().toUpperCase());

            List<LocalDateTime> availableTimes = reservationService.getAvailableTimesForNextMonth(user.getStore().getStoreId(), LocalTime.of(store.getOpen(), 0), LocalTime.of(store.getClose() - 1, 0));

            if (availableTimes.isEmpty()) {
                response.put("isSuccess", false);
                response.put("code", 2002);
                response.put("message", "예약 가능한 시간이 없습니다.");
                status = HttpStatus.NO_CONTENT;
            } else {
                response.put("isSuccess", true);
                response.put("code", 1000);
                response.put("message", "요청에 성공하였습니다.");
                response.put("result", availableTimes);
                status = HttpStatus.OK;
            }
        }

        return new ResponseEntity<>(response, status);
    }


    // 예약 생성
    @ResponseBody
    @PostMapping("/create/booking")
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody ReservationRequest reservationRequest) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if(user == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        try {
            Reservation reservation = reservationService.createReservation(user.getId(), reservationRequest.getStoreId(), reservationRequest.getDogSize(), reservationRequest.getCutStyle(), reservationRequest.getBathStyle());

            Map<String, Object> reservationResult = new LinkedHashMap<>();
            reservationResult.put("reservationId", reservation.getReservationId());
            reservationResult.put("username", reservation.getUsername());
            reservationResult.put("address", reservation.getAddress());
            reservationResult.put("phone", reservation.getUser_phone());
            reservationResult.put("pet_gender", reservation.getPet_gender());
            reservationResult.put("pet_type", reservation.getPet_type());
            reservationResult.put("dogSize", reservation.getDogSize());
            reservationResult.put("cutStyle", reservation.getCutStyle());
            reservationResult.put("bathStyle", reservation.getBathStyle());

            response.put("isSuccess", true);
            response.put("code", 1000);
            response.put("message", "요청에 성공하였습니다.");
            response.put("result", reservationResult);
            status = HttpStatus.CREATED;

        } catch (NoSuchElementException e) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "예약에 실패하였습니다. 상점 또는 사용자를 찾을 수 없습니다.");
            status = HttpStatus.NOT_FOUND;

        } catch (IllegalStateException e) {
            response.put("isSuccess", false);
            response.put("code", 2002);
            response.put("message", "예약에 실패하였습니다. 원인: " + e.getMessage());
            status = HttpStatus.CONFLICT;

        } catch (Exception e) {
            response.put("isSuccess", false);
            response.put("code", 2003);
            response.put("message", "예약에 실패하였습니다. 원인: " + e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    // 예약상세 3
    @ResponseBody
    @PostMapping("/booking/time/{reservationId}")
    public ResponseEntity<Map<String, Object>> insertBookTime(@PathVariable("reservationId") Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        try {
            Reservation inserTIme = reservationService.insertBookingtime(reservationId, startTime);

            Map<String, Object> reservationResult = new LinkedHashMap<>();
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

            response.put("isSuccess", true);
            response.put("code", 1000);
            response.put("message", "요청에 성공하였습니다.");
            response.put("result", reservationResult);
            status = HttpStatus.CREATED;

        } catch (IllegalArgumentException e) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "예약 시간을 지정하지 못했습니다. 원인: " + e.getMessage());
            status = HttpStatus.BAD_REQUEST;

        } catch (NoSuchElementException e) {
            response.put("isSuccess", false);
            response.put("code", 2002);
            response.put("message", "예약이 존재하지 않습니다.");
            status = HttpStatus.NOT_FOUND;

        } catch (Exception e) {
            response.put("isSuccess", false);
            response.put("code", 2003);
            response.put("message", "예약 시간을 지정하는데 실패하였습니다.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    // 예약상세 저장내용 불러오기
    @ResponseBody
    @GetMapping("/userInfo")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        try {
            Map<String, Object> userinfoMap = userService.getUserInfo(user.getId());
            response.put("isSuccess", true);
            response.put("code", 1000);
            response.put("message", "요청에 성공하였습니다.");
            response.put("result", userinfoMap);
            status = HttpStatus.OK;

        } catch (NoSuchElementException e) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "요청하신 사용자 정보를 찾을 수 없습니다.");
            status = HttpStatus.NOT_FOUND;

        } catch (Exception e) {
            response.put("isSuccess", false);
            response.put("code", 2002);
            response.put("message", "사용자 정보를 불러오는데 실패했습니다.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }


    // 예약 수정
    @PutMapping("/my/booking/update/{reservationId}")
    public ResponseEntity<Map<String, Object>> updateReservation(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        try {
            Reservation updatedReservation = reservationService.updateReservation(reservationId, startTime);
            response.put("isSuccess", true);
            response.put("code", 1000);
            response.put("message", "요청에 성공하였습니다.");
            response.put("result", updatedReservation.getStartTime());
            status = HttpStatus.OK;

        } catch (IllegalArgumentException e) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "예약 수정에 실패했습니다. 원인: " + e.getMessage());
            status = HttpStatus.BAD_REQUEST;

        } catch (NoSuchElementException e) {
            response.put("isSuccess", false);
            response.put("code", 2002);
            response.put("message", "해당하는 예약이 존재하지 않습니다.");
            status = HttpStatus.NOT_FOUND;

        } catch (Exception e) {
            response.put("isSuccess", false);
            response.put("code", 2003);
            response.put("message", "예약 수정 과정에서 문제가 발생했습니다.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    // 예약 삭제
    @DeleteMapping("/my/booking/delete/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long reservationId) {

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        try {
            reservationService.deleteReservation(reservationId);
            response.put("isSuccess", true);
            response.put("code", 1000);
            response.put("message", "예약을 삭제하였습니다.");
            status = HttpStatus.OK;
        } catch (IllegalArgumentException e) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "예약을 찾을 수 없습니다. 원인: " + e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }

    // 업체 - 예약관리1
    @ResponseBody
    @GetMapping("/manage/bookings")
    public ResponseEntity<?> ReservationStoreMonth(@RequestParam int year, @RequestParam int month) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (principalDetails == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        User user = principalDetails.getUser();
        Store store = user.getStore();

        List<Reservation> reservations = reservationService.getMonthlyReservationsByStore(store, year, month);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, Object>> responseData = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> reservationData = new LinkedHashMap<>();
            reservationData.put("time", timeFormatter.format(reservation.getStartTime()));
            reservationData.put("petName", reservation.getPet_name());
            reservationData.put("confirmed", reservation.getConfirmed());

            responseData.add(reservationData);
        }


        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "요청에 성공하였습니다.");
        response.put("result", responseData);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 업체 - 예약관리2 예약요청
    @ResponseBody
    @GetMapping("/manage/bookings/requests")
    public ResponseEntity<Map<String, Object>> reservationRequestsList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (principalDetails == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        User user = principalDetails.getUser();

        Page<Reservation> reservationPage = reservationService.getRequest(user.getStore().getStoreId(), false, pageable);
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

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "요청에 성공하였습니다.");
        response.put("result", resultList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // 업체 - 예약관리4 예약완료
    @ResponseBody
    @GetMapping("/manage/bookings/confirmed")
    public ResponseEntity<Map<String, Object>> reservationConfirmedList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (principalDetails == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

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

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "요청에 성공하였습니다.");
        response.put("result", resultList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 예약 상세보기
    @ResponseBody
    @GetMapping("/booking/details/{reservationId}")
    public ResponseEntity<?> reservationRequest(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.getRequestById(reservationId);

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (reservation == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "예약을 찾을 수 없습니다.");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

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

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "요청에 성공하였습니다.");
        response.put("result", reservationMap);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 업체 - 예약승인
    @ResponseBody
    @GetMapping("/manage/bookings/confirm/{reservatonId}")
    public ResponseEntity<?> confirmedReservation(@PathVariable Long reservatonId) {
        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (user.getStore() == null) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        Reservation reservation = reservationService.confirmReservation(reservatonId);

        if (reservation == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "예약을 찾을 수 없습니다.");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "예약승인이 완료되었습니다.");
        response.put("reservationId", reservation.getReservationId());
        response.put("confirmed", reservation.getConfirmed());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 업체 - 예약반려
    @ResponseBody
    @GetMapping("/manage/bookings/reject/{reservationId}")
    public ResponseEntity<?> rejectReservation(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {

        PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalDetails.getUser();

        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (user.getStore() == null) {
            response.put("isSuccess", false);
            response.put("code", 2001);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        Reservation reservation = reservationService.rejectReservation(reservationId, reservationRequest.getCause());

        if (reservation == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "예약을 찾을 수 없습니다");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "예약이 반려되었습니다.");
        response.put("cause", reservationRequest.getCause());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 유저 - 예약내역
    @ResponseBody
    @GetMapping("/my/visit")
    public ResponseEntity<Map<String, Object>> reservationList(@PageableDefault(size = 6, page = 0, sort = "userId") Pageable pageable) {
        PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = new LinkedHashMap<>();
        HttpStatus status;

        if (principalDetails == null) {
            response.put("isSuccess", false);
            response.put("code", 2000);
            response.put("message", "not found user");
            status = HttpStatus.UNAUTHORIZED;
            return new ResponseEntity<>(response, status);
        }

        User user = principalDetails.getUser();

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

        response.put("isSuccess", true);
        response.put("code", 1000);
        response.put("message", "요청에 성공하였습니다.");
        response.put("result", result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}