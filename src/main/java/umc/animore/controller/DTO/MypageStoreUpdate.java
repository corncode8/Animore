package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MypageStoreUpdate {

    private String storeName;

    private String storeExplain;

    private String storeImageUrl;

    private String open;

    private String close;

    private String dayoff1;
    private String dayoff2;

    private String amount;

    private String storeSignificant;

    private List<String> tags;  //해시태그


    public MypageStoreUpdate(String storeName, String storeExplain, String storeImageUrl, String open, String close, String dayoff1, String dayoff2, String amount, String storeSignificant, List<String> tags) {
        this.storeName = storeName;
        this.storeExplain = storeExplain;
        this.storeImageUrl = storeImageUrl;
        this.open = open;
        this.close = close;
        this.dayoff1 = dayoff1;
        this.dayoff2 = dayoff2;
        this.amount = amount;
        this.storeSignificant = storeSignificant;
        this.tags = tags;
    }
}

