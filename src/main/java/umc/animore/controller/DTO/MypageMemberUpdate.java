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

    // private String 생년월일 ?? 받은적 없음.

    private String phone;

    private String gender;

    @Builder
    public MypageMemberUpdate(String email, String password,String nickname,String phone,String gender) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
    }
}
