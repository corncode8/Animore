package umc.animore.model.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.animore.model.Store;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;

    @ManyToOne
    @JoinColumn(name="storeId")
    private Store store;

    private String Service_menu;                // 서비스 명
    private int price;                          // 가격
    private int Available;                      // 서비스 이용 가능 여부
    private LocalDateTime reservation_time;         // 예약 시간


}
