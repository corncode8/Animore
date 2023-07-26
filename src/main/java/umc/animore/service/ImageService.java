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
    public void save(Image image,Long userId,String nickname, String aboutMe){

        User user = userRepository.findById(userId);
        user.setNickname(nickname);
        user.setAboutMe(aboutMe);

        Image img = user.getImage();

        if(img ==null){
            img = new Image();
            img.setUser(user);
        }

        img.setImgName(image.getImgName());
        img.setImgOriName(image.getImgOriName());
        img.setImgPath(image.getImgPath());


        imageRepository.save(img);
    }
}
