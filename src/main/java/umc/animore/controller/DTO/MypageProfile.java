package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageProfile {

    private String nickname;

    private String aboutMe;





    @Builder
    public MypageProfile(String nickname, String aboutMe) {
        this.nickname = nickname;
        this.aboutMe = aboutMe;
    }


}
