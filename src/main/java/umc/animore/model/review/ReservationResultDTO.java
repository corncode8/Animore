package umc.animore.model.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.animore.model.Reservation;
import umc.animore.model.Store;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResultDTO  {
    private Long reservationId;
    private LocalDateTime startTime;        // 예약 시간
    private String pet_name;        // 반려동물 이름
    private String username;        // 보호자 이름
    private String pet_type;        // 반려동물 종류
    private String pet_gender;      // 반려동물 성별
    private Reservation.DogSize dogSize;
    private Reservation.CutStyle cutStyle;
    private Reservation.BathStyle bathStyle;
    private StoreDTO storeDTO;

}
