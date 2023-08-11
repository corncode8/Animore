package umc.animore.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.animore.model.Reservation;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private Long storeId;
    private String startTime;
    private String petname;
    private String cause;
    private Reservation.DogSize dogSize;
    private Reservation.CutStyle cutStyle;
    private Reservation.BathStyle bathStyle;

}

