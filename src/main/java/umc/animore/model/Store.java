package umc.animore.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int store_idx;

    private String Storename;           // 업체 이름
    private String Storeaddress;        // 업체 주소
    private String StoreTEL;            // 업체 번호
    private String StoreExplain;        // 업체 설명 글
    private String StoreSignificant;    // 업체 특징
    private boolean isDiscounted;       // 할인 여부

}
