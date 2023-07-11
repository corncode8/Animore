package com.example.animore.Search.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transient
    private int reviewCount;

    //다대일 관계
    //한 개의 Town이 여러 개의 Store를 가질 수 있지만, 각각의 Store는 하나의 Town에만 속할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id")
    private Town town;

    // town 필드의 Getter
    public Town getTown() {
        return town;
    }

    // town 필드의 Setter
    public void setTown(Town town) {
        this.town = town;
    }

//    // 다대일 관계
//    //가게에는 여러 개의 리뷰를 작성할 수 있고, 리뷰는 한 가게에 하나씩만 작성할 수 있는 관계
//    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviews = new ArrayList<>();
//
//    public int getReviewCount() {
//        return reviewCount;
//    }
//
//    public void setReviewCount(int reviewCount) {
//        this.reviewCount = reviewCount;
//    }

}
