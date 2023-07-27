package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.animore.model.Image;

import java.util.List;

@Getter
@NoArgsConstructor
public class MypageProfile {

    private String nickname;

    private String aboutMe;

    private String imgPath;





    @Builder
    public MypageProfile(String nickname, String aboutMe,String imgPath) {
        this.nickname = nickname;
        this.aboutMe = aboutMe;
        this.imgPath = imgPath;
    }


}
