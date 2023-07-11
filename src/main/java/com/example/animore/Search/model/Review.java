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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    @Column(name = "user_idx")
    private Integer userIdx;
    @Column(name = "pet_id")
    private Integer petId;
    @Column(name = "created_date")
    private String createdDate;
    @Column(name = "modified_date")
    private String modifiedDate;
    @Column(name = "review_content")
    private String reviewContent;
    @Column(name="review_like")
    private double reviewLike;

    //일대다 관계
    //가게에는 여러 개의 리뷰를 작성할 수 있고, 리뷰는 한 가게에 하나씩만 작성할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

//    // store 필드의 Getter
//    public Store getStore() {
//        return store;
//    }
//
//    // store 필드의 Setter
//    public void setStore(Store store) {
//        this.store = store;
//    }

}