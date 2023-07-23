package com.example.animore.Search;

import com.example.animore.Search.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store,Long> {
    //List<Store> findByStoreIdx(Long storeId);

}
