package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageMember {

    private String nickname;

    private String birthday;

    private String phone;

    private String email;

    private String gender;



    @Builder
    public MypageMember(String nickname,String phone,String birthday, String email, String gender) {

        this.nickname = nickname;
        this.phone = phone;
        this.birthday = birthday;
        this.email = email;
        this.gender = gender;
    }

}
