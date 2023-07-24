package umc.animore.controller.DTO;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class MypageHome {

    private String nickname;

    private String petName;

    private String petType;

    private int petAge;

    /**
     * 중성화 0는 뭐 받아야함?
     */

    /**
     * 최근 이용 기록은 ?
     */

    @Builder
    public MypageHome(String nickname, String petName, String petType, int petAge) {
        this.nickname = nickname;
        this.petName = petName;
        this.petType = petType;
        this.petAge = petAge;
    }






}
