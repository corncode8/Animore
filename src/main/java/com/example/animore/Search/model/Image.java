package com.example.animore.Search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Image")
public class Image {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long imageId;

    @Column(name = "img_name")
    private String imgName;

    @Column(name = "img_ori_name")
    private String imgOriName;

    @Column(name = "img_path")
    private String imgPath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "reservationId")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="storeId")
    private Store store;

    // @ManyToOne
    // @JoinColumn(name = "reservation_id")
    // private Reservation reservation;

    //    @ManyToOne
    //    @JoinColumn(name = "reservation_id")
    //    private Reservation reservation;



}