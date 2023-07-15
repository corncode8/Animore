package com.example.animore.Search;

import com.example.Config.BaseException;
import com.example.Config.BaseResponse;
import com.example.animore.Search.model.SearchHistory;
import com.example.animore.Search.model.Store;
import com.example.animore.Search.model.Town;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.Config.BaseResponseStatus.*;

@Service
public class SearchService {

    @Autowired
    private SearchRespository searchRespository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired SearchService(SearchRespository searchRespository, SearchHistoryRepository searchHistoryRepository, TownRepository townRepository, ReviewRepository reviewRepository){
        this.searchRespository = searchRespository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.townRepository =townRepository;
        this.reviewRepository = reviewRepository;
    }

    //가게이름
    public List<Store> searchNameList(String storeName) throws BaseException {
        try {
            List<Store> store = searchRespository.findByStoreNameContaining(storeName);
            return store;
        }catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //가게이름 인기순
    public List<Store> searchNameBestList(String storeName) throws BaseException {
        try {
            List<Store> store = searchRespository.findByStoreNameContainingOrderByStoreLikeDesc(storeName);
            return store;
        }catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //가게이름 후기 많은 순
    public List<Store> searchNameMostReviewsList(String storeName) throws BaseException {
        try {
            List<Store> store = searchRespository.findStoresWithMostReviewsByStoreNameContaining(storeName);
            return store;
        }catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    //주소
    public List<Store> searchLocationList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findByStoreLocationContaining(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //주소 인기순
    public List<Store> searchLocationBestList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findByStoreLocationContainingOrderByStoreLikeDesc(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //주소 후기 많은 순
    public List<Store> searchLocationMostReviesList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findStoresWithMostReviewsByStoreLocationContaining(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    //시'도
    public List<Store> searchCityList(String city, String district) throws BaseException {

        try {
            Town town= townRepository.getTownIdByCityAndDistrict(city, district);
            System.out.println("가게정보: " + town);

            List<Store> store = searchRespository.findByTown(town);
            return store;
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //시도 인기순
    public List<Store> searchCityListBest(String city, String district) throws BaseException {
        try {
            Town town = townRepository.getTownIdByCityAndDistrict(city, district);
            System.out.println("가게정보: "+town);

            List<Store> store = searchRespository.findByTownOrderByStoreLikeDesc(town);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //시도 후기 많은 순
    public List<Store> searchCityListMostReviews(String city, String district) throws BaseException {
        try {
            Town town = townRepository.getTownIdByCityAndDistrict(city, district);
            System.out.println("가게정보: "+town);

            List<Store> store = searchRespository.findStoresWithMostReviewsByTown(town);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //리뷰
    public List<Store> getStoresWithMostReviews() throws BaseException {

        try {
            List<Store> stores = searchRespository.findStoresWithMostReviews();
            stores.forEach(store -> Hibernate.initialize(store.getTown()));
            return stores;
        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

//    //가게이름 - 후기 많은 순
//    public List<Store> searchNameListTop(String storeName) throws BaseException {
//        try {
//            List<Store> store = searchRespository.findByStoreNameContainingOrderByReviewsDesc(storeName);
//            return store;
//        }catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }

//    //가게이름 - 후기 많은 순 (4개씩)
//    public Page<Store> searchNameListTopPage(String storeName, int page) throws BaseException {
//        try {
//            Pageable pageable = PageRequest.of(page,4);
//            Page<Store> store = searchRespository.findByStoreNameContainingOrderByReviewsDesc(storeName,pageable);
//            return store;
//        }catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }



//    //주소 - 후기 많은 순(4개씩)
//    public Page<Store> searchLocationListTopPage(String storeLocation, int page) throws BaseException {
//        try {
//            Pageable pageable = PageRequest.of(page,4);
//            Page<Store> store = searchRespository.findByStoreLocationContainingOrderByReviewsDesc(storeLocation, pageable);
//            return store;
//        }catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }
/*
    //시도 - 후기 많은 순 (4개씩)
    public Page<Store> searchStoresByCityAndDistrict(String city, String district, int page) throws BaseException {
        try {

            //Town townId = townRepository.findTownByCityAndDistrict(city, district);

            Pageable pageable = PageRequest.of(page, 4, Sort.by("reviews").descending());

           // Page<Store> storePage = searchRespository.findByTownIdOrderByReviewsDesc(townId, pageable);

            if (storePage.isEmpty()) {
                // 결과가 없는 경우 빈 페이지 생성
                return Page.empty();
            }

            // 결과가 3개 미만인 경우, 빈 엔티티를 추가하여 출력할 수 있도록 합니다.
            int missingCount = Math.max(4 - storePage.getContent().size(), 0);
            List<Store> content = new ArrayList<>(storePage.getContent());
            for (int i = 0; i < missingCount; i++) {
                content.add(new Store());
            }

            Page<Store> store = new PageImpl<>(content, pageable, storePage.getTotalElements());

            return store;
        }catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

 */

    /*
    public List<Store> searchStoresByCityAndDistrict(String city, String district) throws BaseException {

        try {
            List<Town> towns = townRepository.findByCityAndDistrict(city, district);
            List<Store> stores = new ArrayList<>();
            for (Town town : towns) {
                stores.addAll(town.getStores());
            }
            return stores;

        } catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

     */





//    //후기많은순
//    public List<Store> getTopStoresByReviewCount() throws BaseException {
//        try{
//            List<Store> store = searchRespository.findByOrderByReviewsDesc();
//            return store;
//        }catch (Exception exception){
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }

//    //후기 많은 순 (3개씩) - 메인화면(예약 많은 순이긴 한데 일단 예약 페이지 없어서 후기 순으로 만듦)
//    public Page<Store> getTopStoresByReviewCountPage(int page) throws BaseException {
//        try {
//            Pageable pageable = PageRequest.of(page,3);
//            Page<Store> store = searchRespository.findByOrderByReviewsDesc(pageable);
//            return store;
//        }catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }


    //최근 검색기록 (3개씩)
    public Page<SearchHistory> searchHistory(int userIdx, int page) throws BaseException {
        try {
            Pageable pageable = PageRequest.of(page,3);
            Page<SearchHistory> searchHistoryRes = searchHistoryRepository.findByUserIdxOrderBySearchCreateAtDesc(userIdx, pageable);
            return searchHistoryRes;
        }catch (Exception exception){
            throw  new BaseException(RESPONSE_ERROR);
        }
    }

    public void postSearchHistory(int userIdx, String searchQuery) {
        Page<SearchHistory> searchHistoryPage = searchHistoryRepository.findByUserIdxOrderBySearchCreateAtDesc(userIdx,PageRequest.of(0, 3));

        if (searchHistoryPage.getTotalElements() < 3) {
            saveQuery(userIdx, searchQuery);
        } else {
            List<SearchHistory> searchHistoryList = searchHistoryPage.getContent();
            SearchHistory oldestSearchHistory = searchHistoryList.get(searchHistoryList.size() - 1);
            searchHistoryRepository.delete(oldestSearchHistory);
            saveQuery(userIdx, searchQuery);
        }
    }

    private void saveQuery(int userIdx, String searchQuery) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setUserIdx(userIdx);
        searchHistory.setSearchQuery(searchQuery);
        searchHistory.setSearchCreateAt(String.valueOf(LocalDateTime.now()));
        searchHistoryRepository.save(searchHistory);
    }

}
