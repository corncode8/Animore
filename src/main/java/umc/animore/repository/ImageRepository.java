package umc.animore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import umc.animore.model.Image;
import umc.animore.model.Review;
import umc.animore.model.Store;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImageId(Long Id);
    Page<Image> findByStoreIsDiscounted(boolean isDiscounted, Pageable pageable);



    @Query("SELECT i FROM Image i WHERE i.store.storeId = :storeId")
    Image findByStoreId(Long storeId);

}
