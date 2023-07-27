package umc.animore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    private String pet_name;        // 반려동물 이름
    private String username;        // 보호자 이름
    private String pet_type;        // 반려동물 종류

    private String user_phone;      // 보호자 전화번호
    private String pet_gender;      // 반려동물 성별
    private String address;         // 회원 주소
    private String request;         // 요청사항
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;        // 예약 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer confirmed;       // 예약 확정 여부

    private String cause;           // 예약 반려 사유

    @CreationTimestamp
    private Timestamp create_at;    // 예약 생성 시간
    @UpdateTimestamp
    private Timestamp update_at;    // 예약 수정 시간

}