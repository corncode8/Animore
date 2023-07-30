package umc.animore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Pet;
import umc.animore.model.User;

public interface PetRepository extends JpaRepository<Pet, Integer>{

    Pet findByUser_id(Long userId);

    Pet findByUser(User user);

    Pet findTop1ByUser_idOrderByPetId(Long userId);



}
