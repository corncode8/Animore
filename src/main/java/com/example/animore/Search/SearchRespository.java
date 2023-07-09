package com.example.animore.Search;

import com.example.animore.Search.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchRespository extends JpaRepository<Store, Integer> {

    List<Store> findByStoreNameContaining(String storeName); //가게이름


    List<Store> findByStoreLocationContaining(String storeLocation); //가게주소
}
