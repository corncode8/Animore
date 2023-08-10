package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.exception.BaseException;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypagePetUpdate;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.repository.PetRepository;
import umc.animore.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static umc.animore.config.exception.BaseResponseStatus.GET_PET_EMPTY_ERROR;
import static umc.animore.config.exception.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;


    public Pet findByUserId(Long userId){
        return petRepository.findByUser_id(userId);
    }

    public Pet getPetInfo(User user) {
        return petRepository.findByUser(user);
    }


    @Transactional
    public Pet findTop1ByUser_idOrderByPetId(Long userId){
        return petRepository.findTop1ByUser_idOrderByPetId(userId);
    }




    /**
     * userId로 n개의 pet 조회 후 MypagePetUpdate로 반환
     */
    @Transactional
    public List<MypagePetUpdate> findMypageMPetUpdateByUserId(Long userId) throws BaseException {

        try {

            User user = userRepository.findById(userId);

            List<Pet> pets = user.getPets();
            System.out.println(pets);

            List<MypagePetUpdate> mypagePetUpdates = new ArrayList<MypagePetUpdate>();

            for (Pet pet : pets) {
                MypagePetUpdate mypagePetUpdate = MypagePetUpdate.builder()
                        .petId(pet.getPetId())
                        .petWeight(pet.getPetWeight())
                        .petAge(pet.getPetAge())
                        .petGender(pet.getPetGender())
                        .petName(pet.getPetName())
                        .petSpecials(pet.getPetSpecials())
                        .petType(pet.getPetType())
                        .build();

                mypagePetUpdates.add(mypagePetUpdate);
            }

            if (mypagePetUpdates.size()<1) {
                throw new BaseException(GET_PET_EMPTY_ERROR);
            }

            return mypagePetUpdates;

        }catch(BaseException e){
            throw new BaseException(GET_PET_EMPTY_ERROR);
        }

        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }

    }


    /**
     * mypagePetUpdate를 이용하여 Pet 업데이트
     */

    @Transactional
    public MypagePetUpdate saveMypagePetUpdate(MypagePetUpdate mypagePetUpdate,Long userId) throws BaseException{

        try {

            User user = userRepository.findById(userId);
            List<Pet> pets = user.getPets();


            if(pets == null){
                throw new BaseException(GET_PET_EMPTY_ERROR);
            }

            for (Pet pet : pets) {
                if(pet.getPetId() == mypagePetUpdate.getPetId()) {

                    if (mypagePetUpdate.getPetAge() != 0) {
                        pet.setPetAge(mypagePetUpdate.getPetAge());
                    }
                    if (mypagePetUpdate.getPetName() != null) {
                        pet.setPetName(mypagePetUpdate.getPetName());
                    }
                    if (mypagePetUpdate.getPetGender() != null) {
                        pet.setPetGender(mypagePetUpdate.getPetGender());
                    }
                    if (mypagePetUpdate.getPetType() != null) {
                        pet.setPetType(mypagePetUpdate.getPetType());
                    }
                    if (mypagePetUpdate.getPetSpecials() != null) {
                        pet.setPetSpecials(mypagePetUpdate.getPetSpecials());
                    }
                    if (mypagePetUpdate.getPetWeight() != 0) {
                        pet.setPetWeight(mypagePetUpdate.getPetWeight());
                    }

                    return new MypagePetUpdate().builder()
                            .petId(pet.getPetId())
                            .petAge(pet.getPetAge())
                            .petName(pet.getPetName())
                            .petGender(pet.getPetGender())
                            .petType(pet.getPetType())
                            .petSpecials(pet.getPetSpecials())
                            .petWeight(pet.getPetWeight())
                            .build();
                }
            }

            System.out.println("여기까지 코드가 왔으면 강제에러");
            throw new BaseException(RESPONSE_ERROR);

        }catch(BaseException e){
            throw new BaseException(GET_PET_EMPTY_ERROR);
        }

        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }

    }
}
