package umc.animore.model.review;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import umc.animore.model.Town;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    private Long storeId;
    private String storeName;
    private String storeExplain;
    private String storeImageUrl;
    private String storeLocation;
    private String storeNumber;
    private Integer storeRecent;
    private Integer storeLike; //찜 기능
    private Timestamp createAt;
    private Timestamp modifyAt;
    private double latitude; //위도
    private double longitude; //경도
    private boolean isDiscounted;       // 할인 여부
    private int open;
    private int close;
    private int amount;                 // 최대 예약 건수
    private String dayoff1;
    private String dayoff2;

}
