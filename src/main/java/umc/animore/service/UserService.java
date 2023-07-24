package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umc.animore.model.User;
import umc.animore.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User getUserId(Long userId) {
        return userRepository.findById(userId);
    }

}
