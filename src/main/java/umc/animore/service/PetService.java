package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.controller.DTO.MypagePetUpdate;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.repository.PetRepository;
import umc.animore.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Pet findTop1ByUser_idOrderByPetId(Long userId){
        return petRepository.findTop1ByUser_idOrderByPetId(userId);
    }




    /**
     * userId로 n개의 pet 조회 후 MypagePetUpdate로 반환
     */
    @Transactional
    public List<MypagePetUpdate> findMypageMPetUpdateByUserId(Long userId){
        User user = userRepository.findById(userId);

        List<Pet> pets = user.getPets();
        System.out.println(pets);

        List<MypagePetUpdate> mypagePetUpdates = new ArrayList<MypagePetUpdate>();

        for(Pet pet : pets){
            MypagePetUpdate mypagePetUpdate = MypagePetUpdate.builder()
                    .petWeight(pet.getPetWeight())
                    .petAge(pet.getPetAge())
                    .petGender(pet.getPetGender())
                    .petName(pet.getPetName())
                    .petSpecials(pet.getPetSpecials())
                    .petType(pet.getPetType())
                    .build();

            mypagePetUpdates.add(mypagePetUpdate);
        }

        return mypagePetUpdates;
    }


    /**
     * mypagePetUpdate를 이용하여 Pet 업데이트
     */

    @Transactional
    public void saveMypagePetUpdate(MypagePetUpdate mypagePetUpdate,Long userId){
        User user = userRepository.findById(userId);
        List<Pet> pets = user.getPets();
        Pet pet = null;

        for(Pet tmp : pets){
            if((tmp.getPetName().equals(mypagePetUpdate.getPetName())) && (tmp.getPetWeight() == mypagePetUpdate.getPetWeight())){
                pet = tmp;
            }
        }

        pet.setPetAge(mypagePetUpdate.getPetAge());
        pet.setPetName(mypagePetUpdate.getPetName());
        pet.setPetGender(mypagePetUpdate.getPetGender());
        pet.setPetType(mypagePetUpdate.getPetType());
        pet.setPetSpecials(mypagePetUpdate.getPetSpecials());
        pet.setPetWeight(mypagePetUpdate.getPetWeight());

    }
}
