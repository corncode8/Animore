package umc.animore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;              // 유저_idx

    @ManyToOne
    @JoinColumn(name= "store_id")
    private Store store;            // 업체_idx

    private String pet_name;        // 반려동물 이름
    private String username;        // 보호자 이름
    private String pet_type;        // 반려동물 종류

    private String user_phone;      // 보호자 전화번호
    private String pet_gender;      // 반려동물 성별
    private String address;         // 회원 주소

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;        // 예약 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean getConfirmed() {
        return this.confirmed;
    }
    @ColumnDefault("false")
    private boolean confirmed;      // 예약 확정 여부



    private String cause;           // 예약 반려 사유

    @Enumerated(EnumType.STRING)
    private DogSize dogSize;

    @Enumerated(EnumType.STRING)
    private CutStyle cutStyle;

    @Enumerated(EnumType.STRING)
    private BathStyle bathStyle;

    @CreationTimestamp
    private Timestamp create_at;    // 예약 생성 시간
    @UpdateTimestamp
    private Timestamp update_at;    // 예약 수정 시간

    public enum DogSize {
        MEDIUM, LARGE
    }

    public enum CutStyle {
        SCISSORS_CUT, MACHINE_CUT, SPOTTING_CUT, CLIPPING_CUT, PARTICAL_CUT
    }

    public enum BathStyle {
        BATH, HEALING, CARBONATED
    }
}

