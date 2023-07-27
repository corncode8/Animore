package umc.animore.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    private Long reviewId;
    @Column(name = "pet_id")
    private Long petId;
    @CreationTimestamp
    @Column(name = "created_date")
    private Timestamp createdDate;
    @UpdateTimestamp
    @Column(name = "modified_date")
    private Timestamp modifiedDate;
    @Column(name = "review_content")
    private Timestamp reviewContent;
    @Column(name="review_like")
    private double reviewLike;

    //일대다 관계
    //가게에는 여러 개의 리뷰를 작성할 수 있고, 리뷰는 한 가게에 하나씩만 작성할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    //다대일 관계
    //한 개의 User이 여러 개의 Review를 가질 수 있지만, 각각의 Review는 하나의 User에만 속할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "img_id")

    private List<Image> images = new ArrayList<>();


    // store 필드의 Getter
    public Store getStore() {
        return store;
    }

    // store 필드의 Setter
    public void setStore(Store store) {
        this.store = store;
    }


    // 이미지 리스트 필드의 getter
    public List<Image> getImages() {
        return images;
    }

    // 이미지 리스트 필드의 setter
    public void setImages(List<Image> images) {
        this.images = images;
    }


}
