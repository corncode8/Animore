package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store,Long> {

    Store findByStoreId(Long storeId);

    //해시태그로 찾음
    List<Store> findByTagsIn(List<String> tags);
}

