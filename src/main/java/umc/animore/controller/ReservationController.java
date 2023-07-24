package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import umc.animore.model.Reservation;
import umc.animore.model.reservation.ReservationRequest;
import umc.animore.service.ReservationService;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    /** 향후 한달간 예약 가능한 시간 조회 **/
    @ResponseBody
    @GetMapping("/availableTimes/nextMonth")
    public ResponseEntity<List<LocalDateTime>> getAvailableTimesForNextMonth() {
        List<LocalDateTime> availableTimes = reservationService.getAvailableTimesForNextMonth(LocalTime.of(9, 0), LocalTime.of(18, 0));
        return ResponseEntity.ok(availableTimes);
    }


    /** 예약 생성 **/
    @ResponseBody
    @PostMapping("/res/{userId}")
    public ResponseEntity<String> createReservation(@PathVariable("userId") Long userId,
                                                    @RequestBody ReservationRequest reservationRequest,
                                                    @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(reservationRequest.getStartTime(), formatter);

        System.out.println("userId: " + userId);
        System.out.println("startTime from request: " + reservationRequest.getStartTime());
        System.out.println("request: " + reservationRequest.getRequest());
        System.out.println("storeIdx : " + reservationRequest.getStoreId());

        try {
            Reservation reservation = reservationService.createReservation(userId, startTime, reservationRequest.getRequest(),reservationRequest.getStoreId(), imageFile);
            return ResponseEntity.ok("Reservation created with ID: " + reservation.getReservationId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create reservation: " + e.getMessage());
        }
    }

}
