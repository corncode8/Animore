package com.example.animore.Search.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="reservation_idx")
    private Integer reservationId; //업체 idx
    @Column(name="user_phone")
    private String userPhone; //보호자 전번
    @Column(name = "address")
    private String address; //회원 주소
    @Column(name = "pet_name")
    private String petName; //반려동물 이름
    @Column(name = "user_name")
    private String userName; //보호자 이름
    @Column(name = "pet_type")
    private String petType; //반려동물 종류
    @Column(name = "pet_gender")
    private String petGender; //반려동물 성별
    @Column(name="request")
    private String request; // 요청사항
    @Column(name="res_time")
    private String resTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;        // 예약 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    private Integer confirmed;       // 예약 확정 여부


    @CreationTimestamp
    @Column(name="create_at")
    private Timestamp createAt;    // 예약 생성 시간
    @UpdateTimestamp
    @Column(name="update_at")
    private Timestamp updateAt;    // 예약 수정 시간

//    @ManyToOne
//    @JoinColumn(name="user_id")
//    private User user;              // 유저_idx

    @ManyToOne
    @JoinColumn(name= "store_idx")
    private Store store;            // 업체_idx



}
