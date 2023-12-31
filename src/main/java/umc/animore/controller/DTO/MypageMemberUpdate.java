package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageMemberUpdate {


    private String nickname;

    private String birthday;

    private String phone;



    @Builder
    public MypageMemberUpdate(String nickname,String phone,String birthday) {

        this.nickname = nickname;
        this.phone = phone;
        this.birthday = birthday;
    }
}
