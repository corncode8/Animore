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

    //예약이 가장 많은 순의 가게
    @Query("SELECT r.store FROM Reservation r WHERE r.confirmed = 1 GROUP BY r.store ORDER BY COUNT(r) DESC")
    List<Store> findStoresWithMostReservations();



}
