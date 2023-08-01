package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageStoreUpdate {

    private String storeName;

    private String storeExplain;

    // TODO: 태그

    private String open;

    private String close;

    private String dayoff1;
    private String dayoff2;

    private String amount;

    @Builder
    public MypageStoreUpdate(String storeName, String storeExplain, String open, String close, String dayoff1, String dayoff2, String amount) {
        this.storeName = storeName;
        this.storeExplain = storeExplain;
        this.open = open;
        this.close = close;
        this.dayoff1 = dayoff1;
        this.dayoff2 = dayoff2;
        this.amount = amount;
    }
}
