package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.animore.model.Pet;
import umc.animore.model.User;
import umc.animore.repository.PetRepository;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public Pet findByUserId(Long userId){
        return petRepository.findByUser_id(userId);
    }

    public Pet getPetInfo(User user) {
        return petRepository.findByUser(user);
    }
}
