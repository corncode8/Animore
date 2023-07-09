package com.example.animore.Search.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_idx")
    private Integer storeIdx; //가게 idx
    @Column(name = "store_name")
    private String storeName; //가게 이름
    @Column(name = "store_location")
    private String storeLocation; //가게 주소
    @Column(name = "store_number")
    private String storeNumber; // 가게 번호
    @Column(name = "store_recent")
    private Integer storeRecent; // 가게 최근 예약
    @Column(name = "store_explain")
    private String storeExplain; // 가게 설명글
    @Column(name = "store_image_url")
    private String storeImageUrl; // 가게 이미지 URL
    @Column(name = "create_at")
    private String createAt; // 생성 일자
    @Column(name = "modify_at")
    private String modifyAt; // 수정 일자

}
