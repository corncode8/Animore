package umc.animore.controller.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
public class MypagePetUpdate {

    private Long petId;

    private String petName;

    private String petType;

    private String petGender;

    private double petWeight;

    private int petAge;

    private String petSpecials;

    @Builder
    public MypagePetUpdate(Long petId,String petName, String petType,String petGender, double petWeight,int petAge,String petSpecials) {

        this.petId = petId;
        this.petName = petName;
        this.petGender = petGender;
        this.petType = petType;
        this.petWeight = petWeight;
        this.petAge = petAge;
        this.petSpecials = petSpecials;
    }
}
