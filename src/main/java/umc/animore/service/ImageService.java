package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.model.Image;
import umc.animore.model.User;
import umc.animore.repository.ImageRepository;
import umc.animore.repository.UserRepository;

@Service
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public void save(Image image,Long userId){

        User user = userRepository.findById(userId);
        Image img = new Image();
        img.setImgName(image.getImgName());
        img.setImgOriName(image.getImgOriName());
        img.setImgPath(image.getImgPath());
        img.setUser(user);

        imageRepository.save(img);
    }
}
