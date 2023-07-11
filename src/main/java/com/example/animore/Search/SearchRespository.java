package com.example.animore.Search;

import com.example.animore.Search.model.Store;
import com.example.animore.Search.model.Town;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRespository extends JpaRepository<Store, Integer> {

    //가게이름
    List<Store> findByStoreNameContaining(String storeName);

    //가게주소
    List<Store> findByStoreLocationContaining(String storeLocation);
    
    //가게 시/도
    List<Store> findByTown (Town town);


    //4개 출력
    //List<Store> findByStoreNameContainingOrderByReviewsDesc(String storeName, Pageable pageable);


    //가게이름을 검색 조건으로 하고 후기 개수가 많은 순으로 가게 조회
    //List<Store> findByStoreNameContainingOrderByReviewsDesc(String storeName);

    //가게이름을 검색 조건으로 하고 후기 개수가 많은 순으로 가게 조회 (페이징)
    //Page<Store> findByStoreNameContainingOrderByReviewsDesc(String storeName, Pageable pageable);

    //가게 시,도를 검색 조건으로 하고 후기 개수가 많은 순으로 가게 조회 (페이징)
    //Page<Store> findByTownOrderByReviewsDesc(Town town, Pageable pageable);

    //List<Store> findByTownCityAndTownDistrict(String city, String district);


    //가게주소을 검색 조건으로 하고 후기 개수가 많은 순으로 가게 조회 (페이징)
    //Page<Store> findByStoreLocationContainingOrderByReviewsDesc(String storeLocation, Pageable pageable);

    //후기 개수가 많은 순으로 가게 조회
    //List<Store> findByOrderByReviewsDesc();

    //후기 개수가 많은 순으로 가게 조회 (페이징)
    //Page <Store> findByOrderByReviewsDesc(Pageable pageable);

}
