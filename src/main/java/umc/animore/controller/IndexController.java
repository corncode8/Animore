package umc.animore.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import umc.animore.model.Image;
import umc.animore.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import umc.animore.repository.StoreRepository;
import umc.animore.service.ImageService;

@Controller
public class IndexController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ImageService imageService;

    @ResponseBody
    @GetMapping("/")
    public Page<Image> getImagesByPage(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "3") int size,
                                       @RequestParam(defaultValue = "true") boolean isDiscounted) {
        return imageService.getImagesByPage(page, size, isDiscounted);
    }
}
