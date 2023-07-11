package umc.animore.service;

import umc.animore.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import umc.animore.repository.ImageRepository;
import umc.animore.repository.StoreRepository;

@Service
public class ImageService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Page<Image> getImagesByPage(int pageNumber, int pageSize, boolean isDiscounted) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return imageRepository.findByStoreIsDiscounted(isDiscounted, pageable);
    }
}
