package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageMemberUpdate {

    private String email;

    private String password;

    private String nickname;

    private String birthday;

    private String phone;

    private String gender;


    @Builder
    public MypageMemberUpdate(String email, String password,String nickname,String phone,String gender,String birthday) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
        this.birthday = birthday;
    }
}
