package umc.animore.service;


import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.model.*;
import umc.animore.repository.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static umc.animore.config.exception.BaseResponseStatus.*;


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

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SearchStoreRepository searchStoreRepository;

    @Autowired
    StoreRepository storeRepository;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SearchService(SearchRespository searchRespository, SearchHistoryRepository searchHistoryRepository, TownRepository townRepository, ReviewRepository reviewRepository, LocationRepository locationRepository){
        this.searchRespository = searchRespository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.townRepository =townRepository;
        this.reviewRepository = reviewRepository;
        this.locationRepository = locationRepository;
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

    //가게이름 후기 평점 높은 순
    public List<Store> searchNameReviewsAvgList(String storeName) throws BaseException {
        try {
            List<Store> store = searchRespository.findStoresWithHighestAverageScoreByStoreNameContaining(storeName);
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
    public List<Store> searchLocationMostReviewsList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findStoresWithMostReviewsByStoreLocationContaining(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //주소 후기 평점 평균 높은 순
    public List<Store> searchLocationReviewsAvgList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findStoresWithHighestAverageScoreByStoreLocationContaining(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    //시도
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

    //시도 후기 평점 평균 높은 순
    public List<Store> searchCityListReviewsAvg(String city, String district) throws BaseException {
        try {
            Town town = townRepository.getTownIdByCityAndDistrict(city, district);
            System.out.println("가게정보: "+town);

            List<Store> store = searchRespository.findStoresWithHighestAverageScoreByTown(town);
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

    //거리순 - 가게이름
        //* 가게이름을 완전히 똑같게 입력하지않으면 equals 작동안되지않나?
        //** 목록 갱신할때 CLEAR()해버리면 가장 가까운 가게만 List에 남을텐데 의도한건가?

    public List<Store> recommendNearestStore(String storeName) throws BaseException {
        try {
            Optional<Location> optionalLocation = Optional.ofNullable(locationRepository.findByLocationId(1L));
            if (optionalLocation.isPresent()) {
                Location currentLocation = optionalLocation.get();

                List<Store> allStores = searchRespository.findAll();

                List<Store> nearestStores = new ArrayList<>();
                double minDistance = Double.MAX_VALUE;

                for (Store store : allStores) {
                    if (store.getStoreName().equals(storeName)) {
                        // 가게와 현재 위치 간의 거리 계산
                        double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                store.getLatitude(), store.getLongitude());

                        // 현재 최소 거리와 같은 경우 가까운 가게 목록에 추가합니다
                        if (distance == minDistance) {
                            nearestStores.add(store);
                        }

                        // 현재 최소 거리보다 작은 경우 가장 가까운 가게 목록을 갱신합니다
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestStores.clear();
                            nearestStores.add(store);
                        }
                    }
                }

                // 최소 3개 이상의 가게가 없다면 1개 또는 2개를 추가합니다.
                if (nearestStores.size() < 3) {
                    int remaining = 3 - nearestStores.size();
                    for (Store store : allStores) {
                        if (store.getStoreName().equals(storeName)) {
                            if (!nearestStores.contains(store)) {
                                nearestStores.add(store);
                                remaining--;
                                if (remaining == 0) {
                                    break; // 최대 3개까지만 추가합니다.
                                }
                            }
                        }
                    }
                }

                return nearestStores;
            }
            return null;
        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    //거리순 - 가게주소
    public List<Store> recommendNearestStoreLocation(String storeLocation) throws BaseException {
        try {
            Optional<Location> optionalLocation = Optional.ofNullable(locationRepository.findByLocationId(1L));
            if (optionalLocation.isPresent()) {
                Location currentLocation = optionalLocation.get();

                List<Store> allStores = searchRespository.findAll();

                List<Store> nearestStores = new ArrayList<>();
                double minDistance = Double.MAX_VALUE;

                for (Store store : allStores) {
                    if (store.getStoreLocation().equals(storeLocation)) {
                        // 가게와 현재 위치 간의 거리 계산
                        double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                store.getLatitude(), store.getLongitude());

                        // 현재 최소 거리와 같은 경우 가까운 가게 목록에 추가합니다
                        if (distance == minDistance) {
                            nearestStores.add(store);
                        }

                        // 현재 최소 거리보다 작은 경우 가장 가까운 가게 목록을 갱신합니다
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestStores.clear();
                            nearestStores.add(store);
                        }
                    }
                }

                // 최소 3개 이상의 가게가 없다면 1개 또는 2개를 추가합니다.
                if (nearestStores.size() < 3) {
                    int remaining = 3 - nearestStores.size();
                    for (Store store : allStores) {
                        if (store.getStoreLocation().equals(storeLocation)) {
                            if (!nearestStores.contains(store)) {
                                nearestStores.add(store);
                                remaining--;
                                if (remaining == 0) {
                                    break; // 최대 3개까지만 추가합니다.
                                }
                            }
                        }
                    }
                }

                return nearestStores;
            }
            return null;
        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    //거리순 - 가게시도
    public List<Store> recommendNearestStoreTown(String city, String district) throws BaseException {
        try {
            Town town = townRepository.getTownIdByCityAndDistrict(city, district);
            Optional<Location> optionalLocation = Optional.ofNullable(locationRepository.findByLocationId(1L));
            if (optionalLocation.isPresent()) {
                Location currentLocation = optionalLocation.get();

                List<Store> allStores = searchRespository.findAll();

                List<Store> nearestStores = new ArrayList<>();
                double minDistance = Double.MAX_VALUE;

                for (Store store : allStores) {
                    if (store.getTown().equals(town)) {
                        // 가게와 현재 위치 간의 거리 계산
                        double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                store.getLatitude(), store.getLongitude());

                        // 현재 최소 거리와 같은 경우 가까운 가게 목록에 추가합니다
                        if (distance == minDistance) {
                            nearestStores.add(store);
                        }

                        // 현재 최소 거리보다 작은 경우 가장 가까운 가게 목록을 갱신합니다
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestStores.clear();
                            nearestStores.add(store);
                        }
                    }
                }

                // 최소 3개 이상의 가게가 없다면 1개 또는 2개를 추가합니다.
                if (nearestStores.size() < 3) {
                    int remaining = 3 - nearestStores.size();
                    for (Store store : allStores) {
                        if (store.getTown().equals(town)) {
                            if (!nearestStores.contains(store)) {
                                nearestStores.add(store);
                                remaining--;
                                if (remaining == 0) {
                                    break; // 최대 3개까지만 추가합니다.
                                }
                            }
                        }
                    }
                }

                return nearestStores;
            }
            return null;
        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;

        return distance;
    }

    //최근 검색기록 (3개씩)
    public List<SearchHistory> searchHistory(User user) throws BaseException {
        try {
            List<SearchHistory> searchHistoryRes = searchHistoryRepository.findByUserOrderBySearchCreateAtDesc(user);
            return searchHistoryRes;
        }catch (Exception exception){
            throw  new BaseException(RESPONSE_ERROR);
        }
    }


    public void postSearchHistory(User user, String searchQuery) {
        List<SearchHistory> searchHistoryList = searchHistoryRepository.findByUserOrderBySearchCreateAtDesc(user);

        if (searchHistoryList.size() < 3) {
            saveQuery(user, searchQuery);
        } else {
            SearchHistory oldestSearchHistory = searchHistoryList.get(searchHistoryList.size() - 1);
            searchHistoryRepository.delete(oldestSearchHistory);
            saveQuery(user, searchQuery);
        }
    }



    private void saveQuery(User user, String searchQuery) {
        SearchHistory searchHistory = new SearchHistory();

        // 현재 시간을 기준으로 Timestamp 객체 생성
        Timestamp timestamp = Timestamp.from(Instant.now());

        searchHistory.setUser(user);
        searchHistory.setSearchQuery(searchQuery);

        //SearchHistory 객체에 Timestamp 할당
        searchHistory.setSearchCreateAt(timestamp);
        searchHistoryRepository.save(searchHistory);
    }


    //예약 많은 순
    public List<Store> searchReservationMost() throws BaseException {
        try{
            List<Store> store = searchRespository.findStoresWithMostReservations();
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //가게 검색 기록(3개씩)
    public List<SearchStore> searchStore(User user) throws BaseException{
        try{
            List<SearchStore> searchStoreRes = searchStoreRepository.findByUserOrderBySearchCreateAtDesc(user);
            return searchStoreRes;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }


    public void postSearchStoreHistory(User user, Store store){
        List<SearchStore> searchStores = searchStoreRepository.findByUserOrderBySearchCreateAtDesc(user);

        if(searchStores.size()<3){
            saveStoreRecord(user,store);
        }else{
            SearchStore oldestSearchStore = searchStores.get(searchStores.size()-1);
            searchStoreRepository.delete(oldestSearchStore);
            saveStoreRecord(user,store);
        }
    }


    private void saveStoreRecord(User user, Store store){
        SearchStore searchStore = new SearchStore();

        // 현재 시간을 기준으로 Timestamp 객체 생성
        Timestamp timestamp = Timestamp.from(Instant.now());

        searchStore.setSearchCreateAt(timestamp);
        searchStore.setUser(user);
        searchStore.setStore(store);
        searchStoreRepository.save(searchStore);

    }

    //해시태그로 가게찾음
    public List<Store> searchStoresBytags(List<String> tags) throws BaseException{
        try{
            List<Store> store = storeRepository.findByTagsIn(tags);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }

    }

    //해시태그 인기순
    public List<Store> searchTagsBest(List<String> tags) throws BaseException {
        try {

            List<Store> store = searchRespository.findByTagsInOrderByStoreLikeDesc(tags);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //해시태그 후기 많은 순
    public List<Store> searchTagsMostReviews(List<String> tags) throws BaseException {
        try {
            List<Store> store = searchRespository.findStoresWithMostReviewsByTagsIn(tags);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //해시태그 후기별점 평균 순
    public List<Store> searchTagsReviewsAvg(List<String> tags) throws BaseException {
        try {
            List<Store> store = searchRespository.findStoresWithHighestAverageScoreByTagsIn(tags);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }



    public List<Store> recommendNearestHashTags(List<String> tags) throws BaseException {
        try {
            Optional<Location> optionalLocation = Optional.ofNullable(locationRepository.findByLocationId(1L));
            if (optionalLocation.isPresent()) {
                Location currentLocation = optionalLocation.get();

                List<Store> allStores = searchRespository.findAll();

                List<Store> nearestStores = new ArrayList<>();
                double minDistance = Double.MAX_VALUE;

                for (Store store : allStores) {
                    if (store.getTags().containsAll(tags)) {
                        // 가게와 현재 위치 간의 거리 계산
                        double distance = calculateDistance(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                store.getLatitude(), store.getLongitude());

                        // 현재 최소 거리와 같은 경우 가까운 가게 목록에 추가합니다
                        if (distance == minDistance) {
                            nearestStores.add(store);
                        }

                        // 현재 최소 거리보다 작은 경우 가장 가까운 가게 목록을 갱신합니다
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestStores.clear();
                            nearestStores.add(store);
                        }
                    }
                }

                // 최소 3개 이상의 가게가 없다면 1개 또는 2개를 추가합니다.
                if (nearestStores.size() < 3) {
                    int remaining = 3 - nearestStores.size();
                    for (int i = 0; i < remaining; i++) {
                        nearestStores.add(allStores.get(i)); // 가게 목록에서 앞에서부터 추가합니다.
                    }
                }

                return nearestStores;
            }
            return null;
        } catch (Exception exception) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }


}
