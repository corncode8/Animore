package com.example.animore.Search;

import com.example.animore.Search.model.Store;
import com.example.animore.Search.model.Town;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRespository extends JpaRepository<Store, Integer> {

    //가게이름
    List<Store> findByStoreNameContaining(String storeName);

    //가게이름 인기순
    List<Store> findByStoreNameContainingOrderByStoreLikeDesc(String storeName);

    //가게이름 후기많은 순
    @Query("SELECT r.store FROM Review r WHERE r.store.storeName = :storeName GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReviewsByStoreNameContaining(String storeName);

    //가게이름 후기별점 평균순
    @Query("SELECT r.store, AVG(r.reviewLike) as avgScore FROM Review r WHERE r.store.storeName = :storeName GROUP BY r.store ORDER BY avgScore DESC")
    List<Store> findStoresWithHighestAverageScoreByStoreNameContaining(String storeName);


    //가게주소
    List<Store> findByStoreLocationContaining(String storeLocation);

    //가게주소 인기순
    List<Store> findByStoreLocationContainingOrderByStoreLikeDesc(String storeLocation);

    //가게주소 후기 많은 순
    @Query("SELECT r.store FROM Review r WHERE r.store.storeLocation = :storeLocation GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReviewsByStoreLocationContaining(String storeLocation);


    //가게주소 후기별점 평균순
    @Query("SELECT r.store, AVG(r.reviewLike) as avgScore FROM Review r WHERE r.store.storeLocation = :storeLocation GROUP BY r.store ORDER BY avgScore DESC")
    List<Store> findStoresWithHighestAverageScoreByStoreLocationContaining(String storeLocation);
    
    //가게 시/도
    List<Store> findByTown (Town town);

    //가게 시/도의 인기순
    List<Store> findByTownOrderByStoreLikeDesc(Town town);

    //가게 시/도의 후기 많은 순
    @Query("SELECT r.store FROM Review r WHERE r.store.town = :town GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReviewsByTown(Town town);


    //가게 시/도 후기별점 평균순
    @Query("SELECT r.store, AVG(r.reviewLike) as avgScore FROM Review r WHERE r.store.town = :town GROUP BY r.store ORDER BY avgScore DESC")
    List<Store> findStoresWithHighestAverageScoreByTown(Town town);

    //가장 많은 리뷰를 작성한 가게
    @Query("SELECT r.store FROM Review r GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReviews();


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
