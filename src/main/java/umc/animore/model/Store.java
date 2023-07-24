package umc.animore.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer storeId; //업체 id
    @Column(name = "store_name")
    private String storeName; //업체 이름
    @Column(name = "store_location")
    private String storeLocation; //업체 주소
    @Column(name = "store_number")
    private String storeNumber; // 업체 번호
    @Column(name = "store_recent")
    private Integer storeRecent; // 업체 최근 예약
    @Column(name = "store_explain")
    private String storeExplain; // 업체 설명글
    @Column(name = "store_image_url")
    private String storeImageUrl; // 업체 이미지 URL
    @Column(name = "create_at")
    private String createAt; // 생성 일자
    @Column(name = "modify_at")
    private String modifyAt; // 수정 일자
    @Column(name = "store_like")
    private Integer storeLike; //찜 기능
    @Column(name="latitude")
    private double latitude; //위도
    @Column(name="longitude")
    private double longitude; //경도

    private boolean isDiscounted;       // 할인 여부


    //다대일 관계
    //한 개의 Town이 여러 개의 Store를 가질 수 있지만, 각각의 Store는 하나의 Town에만 속할 수 있는 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "town_id")
    @JsonIgnore
    private Town town;

    // town 필드의 Getter
    public Town getTown() {
        return town;
    }

    // town 필드의 Setter
    public void setTown(Town town) {
        this.town = town;
    }


}
