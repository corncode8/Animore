package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.model.Reservation;
import umc.animore.model.Store;
import umc.animore.model.reservation.PaginationDto;
import umc.animore.model.reservation.ReservationRequest;
import umc.animore.repository.StoreRepository;
import umc.animore.service.ReservationService;
import umc.animore.service.StoreService;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreRepository storeRepository;


    // 향후 한달간 예약 가능한 시간 조회
    @ResponseBody
    @GetMapping("/booking/Calendar")
    public ResponseEntity<List<LocalDateTime>> getAvailableTimesForNextMonth(Long storeId) {
        if (storeId == null) {
            System.out.println("storeId = " + storeId);
        }
        System.out.println(storeId);
        Store store = storeService.getStoreId(storeId);
        if (store == null) {
            System.out.println("해당 store가 없습니다.");
        }

        List<LocalDateTime> availableTimes = reservationService.getAvailableTimesForNextMonth(storeId, LocalTime.of(store.getOpen(), 0), LocalTime.of(store.getClose() - 1, 0));
        return ResponseEntity.ok(availableTimes);
    }


    // 예약 생성
    @ResponseBody
    @PostMapping("/booking/{userId}")
    public ResponseEntity<String> createReservation(@PathVariable("userId") Long userId,
                                                    @RequestBody ReservationRequest reservationRequest,
                                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        System.out.println("userId: " + userId);
        System.out.println("startTime from request: " + reservationRequest.getStartTime());
        System.out.println("request: " + reservationRequest.getRequest());
        System.out.println("storeId : " + reservationRequest.getStoreId());

        try {
            Reservation reservation = reservationService.createReservation(userId, startTime, reservationRequest.getRequest(), reservationRequest.getStoreId(), imageFile);
            return ResponseEntity.ok("Reservation created with ID: " + reservation.getReservationId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create reservation: " + e.getMessage());
        }
    }

    // 예약 수정
    @PutMapping("/booking/update/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long reservationId, @RequestBody ReservationRequest reservationRequest) {
        System.out.println("처음 print : " + reservationRequest.getStartTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);
        System.out.println(startTime.toString());

        try {
            Reservation updatedReservation = reservationService.updateReservation(reservationId, startTime, reservationRequest.getRequest());
            // 예약 수정 성공 시 상태 코드 200
            return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 예약이 존재하지 않거나 겹치는 경우 오류메시지 반환, 상태 코드 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 예약 삭제
    @DeleteMapping("/booking/delete/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long reservationId) {
        try {
            reservationService.deleteReservation(reservationId);
            // 삭제 성공시 204로 설정
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            // 예약을 찾을 수 없는 경우 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 업체 - 예약관리1
    @ResponseBody
    @GetMapping("/manage/{storeId}")
    public List<Map<String, Object>> ReservationStoreMonth(@PathVariable Long storeId, @RequestParam int year, @RequestParam int month) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("StoreId not found"));
        List<Reservation> reservations = reservationService.getMonthlyReservationsByStore(store, year, month);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Map<String, Object>> responseData = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> reservationData = new HashMap<>();
            reservationData.put("time", timeFormatter.format(reservation.getStartTime()));
            reservationData.put("petName", reservation.getPet_name());
            reservationData.put("confirmed", reservation.getConfirmed());

            responseData.add(reservationData);
        }
        return responseData;
    }

    // 업체 - 예약관리2
    @ResponseBody
    @GetMapping("/requests")
    public ResponseEntity<?> reservationRequestsList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        Page<Reservation> reservationPage = reservationService.getRequest(0, pageable);

        PaginationDto paginationDto = new PaginationDto(reservationPage);
        List<Reservation> reservationList = reservationPage.getContent();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Reservation r : reservationList) {
            Map<String, Object> reservationMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String dateString = r.getStartTime().format(formatter);
            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
            String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MM.dd.HH:mm"));
            reservationMap.put("예약일자", formattedDate);
            reservationMap.put("반려동물 이름", r.getPet_name());
            reservationMap.put("보호자 이름", r.getUsername());
            reservationMap.put("보호자 연락처", r.getUser_phone());
            resultList.add(reservationMap);
        }

        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }
    // 업체 - 예약관리4
    @ResponseBody
    @GetMapping("/confirmed")
    public ResponseEntity<?> reservationConfirmedList
    (@PageableDefault(size = 6, page = 0, sort = "reservationId") Pageable pageable) {
        Page<Reservation> reservationPage = reservationService.getRequest(1, pageable);

        List<Reservation> reservationList = reservationPage.getContent();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Reservation r : reservationList) {
            Map<String, Object> reservationMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String dateString = r.getStartTime().format(formatter);
            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
            String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MM.dd.HH:mm"));
            reservationMap.put("예약일자", formattedDate);
            reservationMap.put("반려동물 이름", r.getPet_name());
            reservationMap.put("보호자 이름", r.getUsername());
            reservationMap.put("보호자 연락처", r.getUser_phone());
            resultList.add(reservationMap);
        }

        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    // 업체 - 예약관리 3, 5
    @ResponseBody
    @GetMapping("/details/booking/{reservationId}")
    public ResponseEntity<?> reservationRequest(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.getRequestById(reservationId);
        Map<String, Object> reservationMap = new HashMap<>();
        reservationMap.put("petName", reservation.getPet_name());
        reservationMap.put("petSpecies", reservation.getPet_type());
        reservationMap.put("petGender", reservation.getPet_gender());
        reservationMap.put("userName", reservation.getUsername());
        reservationMap.put("userPhone", reservation.getUser_phone());
        reservationMap.put("userAddress", reservation.getAddress());
        reservationMap.put("requestMessage", reservation.getRequest());
        return new ResponseEntity<>(reservationMap, HttpStatus.OK);
    }

    // 업체 - 예약승인
    @ResponseBody
    @GetMapping("/booking/confirm")
    public ResponseEntity<?> confirmedReservation(@PathVariable Long reservatonId) {
        Reservation reservation = reservationService.confirmReservation(reservatonId);
        return ResponseEntity.status(HttpStatus.OK).body("예약 승인이 완료되었습니다.");
    }

    // 업체 - 예약반려
    @ResponseBody
    @GetMapping("/booking/reject")
    public ResponseEntity<?> rejectReservation(@PathVariable Long reservationId, String cause) {
        Reservation reservation = reservationService.rejectReservation(reservationId, cause);
        return ResponseEntity.status(HttpStatus.OK).body("예약이 반려되었습니다.");
    }

    // 유저 - 예약내역
    @ResponseBody
    @GetMapping("/my/visit/{userId}")
    public ResponseEntity<?> reservationList(@PathVariable Long userId, @PageableDefault(size = 6, page = 0, sort = "userId") Pageable pageable) {
        Page<Reservation> reservationlist = reservationService.getReservationlist(userId, pageable);

        List<Reservation> reservations = reservationlist.getContent();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Reservation i : reservations) {
            Map<String,Object> reservationMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String date = i.getStartTime().format(formatter);
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MM.dd.HH:mm"));
            reservationMap.put("예약일자", formattedDate);
            reservationMap.put("매장명", i.getStore().getStoreName());
            reservationMap.put("매장 주소", i.getStore().getStoreLocation());
            reservationMap.put("매장 연락처", i.getStore().getStoreNumber());

            result.add(reservationMap);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}