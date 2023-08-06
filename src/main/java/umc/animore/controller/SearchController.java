package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.config.exception.BaseResponseStatus;
import umc.animore.model.*;
import umc.animore.model.review.ImageDTO;
import umc.animore.model.review.RecordDTO;
import umc.animore.model.review.StoreDTO;
import umc.animore.repository.StoreRepository;
import umc.animore.repository.UserRepository;
import umc.animore.service.SearchService;

import java.util.ArrayList;
import java.util.List;

import static umc.animore.config.exception.BaseResponseStatus.*;


@RestController
public class SearchController {


    private final SearchService searchService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StoreRepository storeRepository;


    @Autowired
    public SearchController(SearchService searchService){
        this.searchService = searchService;
        //this.jwtService = jwtService;
    }


    //가게이름 검색 API - 검색화면
    //특정 사용자의 모든 리뷰 조회
    @ResponseBody
    @GetMapping("/search/name")
    public BaseResponse<List<StoreDTO>> searchName(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchNameList(storeName);
            searchService.postSearchHistory(user, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    private List<StoreDTO> convertStoreToDTO(List<Store> storeList) {
        List<StoreDTO> storeDTOList = new ArrayList<>();

        for (Store store : storeList) {
            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setStoreId(store.getStoreId());
            storeDTO.setStoreName(store.getStoreName());
            storeDTO.setStoreExplain(store.getStoreExplain());
            storeDTO.setStoreLocation(store.getStoreLocation());
            storeDTO.setStoreImageUrl(store.getStoreImageUrl());
            storeDTO.setStoreNumber(store.getStoreNumber());
            storeDTO.setStoreRecent(store.getStoreRecent());
            storeDTO.setStoreLike(store.getStoreLike());
            storeDTO.setCreateAt(store.getCreateAt());
            storeDTO.setModifyAt(store.getModifyAt());
            storeDTO.setLatitude(store.getLatitude());
            storeDTO.setLongitude(store.getLongitude());
            storeDTO.setDiscounted(store.isDiscounted());
            storeDTO.setOpen(store.getOpen());
            storeDTO.setClose(store.getClose());
            storeDTO.setAmount(store.getAmount());
            storeDTO.setDayoff1(store.getDayoff1());
            storeDTO.setDayoff2(store.getDayoff2());
            storeDTO.setTags(store.getTags());

            storeDTOList.add(storeDTO);
        }

        return storeDTOList;
    }


    //가게이름 검색 인기순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/best")
    public BaseResponse<List<StoreDTO>> searchNameBest(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchNameBestList(storeName);
            searchService.postSearchHistory(user, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    //가게이름 검색 후기많은순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/top_reviews")
    public BaseResponse<List<StoreDTO>> searchNameMostReviews(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchNameMostReviewsList(storeName);
            searchService.postSearchHistory(user, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 후기 평점 평균 높은 순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/avg")
    public BaseResponse<List<StoreDTO>> searchNameReviewsAVG(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchNameReviewsAvgList(storeName);
            searchService.postSearchHistory(user, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/recommend")
    public BaseResponse<List<StoreDTO>> searchNameRecommend(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.recommendNearestStore(storeName);
            searchService.postSearchHistory(user, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //주소 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/location")
    public BaseResponse<List<StoreDTO>> searchLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchLocationList(storeLocation);
            searchService.postSearchHistory(user, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    //주소 검색 인기순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/best")
    public BaseResponse<List<StoreDTO>> searchBestLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchLocationBestList(storeLocation);
            searchService.postSearchHistory(user, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


    //주소 검색 후기 많은 순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/top_reviews")
    public BaseResponse<List<StoreDTO>> searchMostReviewsLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchLocationMostReviewsList(storeLocation);
            searchService.postSearchHistory(user, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    //주소 검색 후기 평점 평균 높은 순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/avg")
    public BaseResponse<List<StoreDTO>> searchReviewsAvgLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchLocationReviewsAvgList(storeLocation);
            searchService.postSearchHistory(user, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    //가게주소 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/location/recommend")
    public BaseResponse<List<StoreDTO>> searchLocationRecommend(@RequestParam(value = "query") String storeLocation) {
        try {

            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.recommendNearestStoreLocation(storeLocation);
            searchService.postSearchHistory(user, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town")
    public BaseResponse<List<StoreDTO>> searchByTown(@RequestParam("city") String city, @RequestParam("district") String district) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchCityList(city,district);
            searchService.postSearchHistory(user, city + " " + district);

            System.out.println("query: " + city + " " + district);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 인기순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/best")
    public BaseResponse<List<StoreDTO>> searchBestByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchCityListBest(city, district);
            searchService.postSearchHistory(user, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 후기 많은 순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/top_reviews")
    public BaseResponse<List<StoreDTO>> searchMostReviewsByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchCityListMostReviews(city, district);
            searchService.postSearchHistory(user, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 후기 평점 평균 높은순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/avg")
    public BaseResponse<List<StoreDTO>> searchReviewsAvgByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchCityListReviewsAvg(city, district);
            searchService.postSearchHistory(user, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/recommend")
    public BaseResponse<List<StoreDTO>> searchByTownRecommend(@RequestParam("city") String city, @RequestParam("district") String district) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            PrincipalDetails principalDetails = (PrincipalDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.recommendNearestStoreTown(city,district);
            searchService.postSearchHistory(user, city + " " + district);

            System.out.println("query: " + city + " " + district);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //후기많은순 API  - 메인화면
    //GET /search/top_reviews
    @ResponseBody
    @GetMapping("/search/top_reviews")
    public BaseResponse<List<StoreDTO>> searchTopreview() {
        try {
            List<Store> store = searchService.getStoresWithMostReviews();
            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //예약많은순 API  - 메인화면
    //GET /search/top_reservation
    @ResponseBody
    @GetMapping("/search/top_reservation")
    public BaseResponse<List<StoreDTO>> searchTopreservation() {
        try {
            List<Store> store = searchService.searchReservationMost();
            List<StoreDTO> resultStore = convertStoreToDTO(store);

            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


    //최근 검색어 API (3개씩) - 메인화면
    //GET /search/recent
    @ResponseBody
    @GetMapping("/search/recent")
    public BaseResponse<List<SearchHistory>> searchHistory() {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<SearchHistory> searchHistory = searchService.searchHistory(user);
            return new BaseResponse<>(searchHistory);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    //가게 조회 - 이렇게 해야 가게를 검색한 것을 저장할 수 있음
    @ResponseBody
    @GetMapping("/search/{storeId}")
    public BaseResponse<StoreDTO> searchStore(@PathVariable Long storeId) {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            Store store = storeRepository.findByStoreId(storeId);

            searchService.postSearchStoreHistory(user, store);

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setStoreId(store.getStoreId());
            storeDTO.setStoreName(store.getStoreName());
            storeDTO.setStoreExplain(store.getStoreExplain());
            storeDTO.setStoreLocation(store.getStoreLocation());
            storeDTO.setStoreImageUrl(store.getStoreImageUrl());
            storeDTO.setStoreNumber(store.getStoreNumber());
            storeDTO.setStoreRecent(store.getStoreRecent());
            storeDTO.setStoreLike(store.getStoreLike());
            storeDTO.setCreateAt(store.getCreateAt());
            storeDTO.setModifyAt(store.getModifyAt());
            storeDTO.setLatitude(store.getLatitude());
            storeDTO.setLongitude(store.getLongitude());
            storeDTO.setDiscounted(store.isDiscounted());
            storeDTO.setOpen(store.getOpen());
            storeDTO.setClose(store.getClose());
            storeDTO.setAmount(store.getAmount());
            storeDTO.setDayoff1(store.getDayoff1());
            storeDTO.setDayoff2(store.getDayoff2());
            storeDTO.setTags(store.getTags());

            return new BaseResponse<>(storeDTO);
        } catch (Exception exception) {
            return new BaseResponse<>(BaseResponseStatus.SERVER_ERROR);
        }
    }


    //최근 가게 검색기록
    @ResponseBody
    @GetMapping("/search/recordstore")
    public BaseResponse<List<RecordDTO>> searchHistoryStore() {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);

            // 사용자의 검색 기록을 가져옴
            List<SearchStore> searchStore = searchService.searchStore(user);
            List<RecordDTO> recordDTOList = new ArrayList<>();

            // 검색 기록 목록을 순회하며 RecordDTO 객체들을 생성하고 리스트에 추가
            for (SearchStore searchStoreItem : searchStore) {
                RecordDTO recordDTO = new RecordDTO();
                recordDTO.setRecordId(searchStoreItem.getRecordId());
                recordDTO.setSearchCreateAt(searchStoreItem.getSearchCreateAt());
                recordDTO.setUserId(user.getId());

                StoreDTO storeDTO = new StoreDTO();
                Store store = searchStoreItem.getStore(); // SearchStore에서 Store를 가져오는 메서드가 있다고 가정합니다.
                storeDTO.setStoreId(store.getStoreId());
                storeDTO.setStoreName(store.getStoreName());
                storeDTO.setStoreExplain(store.getStoreExplain());
                storeDTO.setStoreLocation(store.getStoreLocation());
                storeDTO.setStoreImageUrl(store.getStoreImageUrl());
                storeDTO.setStoreNumber(store.getStoreNumber());
                storeDTO.setStoreRecent(store.getStoreRecent());
                storeDTO.setStoreLike(store.getStoreLike());
                storeDTO.setCreateAt(store.getCreateAt());
                storeDTO.setModifyAt(store.getModifyAt());
                storeDTO.setLatitude(store.getLatitude());
                storeDTO.setLongitude(store.getLongitude());
                storeDTO.setDiscounted(store.isDiscounted());
                storeDTO.setOpen(store.getOpen());
                storeDTO.setClose(store.getClose());
                storeDTO.setAmount(store.getAmount());
                storeDTO.setDayoff1(store.getDayoff1());
                storeDTO.setDayoff2(store.getDayoff2());
                storeDTO.setTags(store.getTags());

                recordDTO.setStoreDTO(storeDTO);
                recordDTOList.add(recordDTO);
            }

            return new BaseResponse<>(recordDTOList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //해시태그
    // GET /search/hashtags?tags=픽업가능,스파가능,피부병치료
    @ResponseBody
    @GetMapping("/search/hashtags")
    public BaseResponse<List<StoreDTO>> searchStoresByHashtags(@RequestParam List<String> tags) {
        try {
            if (tags == null || tags.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchStoresBytags(tags);

            String hashtags = String.join(",", tags);
            searchService.postSearchHistory(user, hashtags);
            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);
            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //해시태그- 인기순
    // GET /search/hashtags/best?tags=픽업가능,스파가능,피부병치료
    @ResponseBody
    @GetMapping("/search/hashtags/best")
    public BaseResponse<List<StoreDTO>> searchBestByHashTags(@RequestParam List<String> tags) {
        try {
            if (tags == null || tags.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchTagsBest(tags);

            String hashtags = String.join(",", tags);
            searchService.postSearchHistory(user, hashtags);
            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);
            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //해시태그- 후기 많은 순
    // GET /search/hashtags/top_review?tags=픽업가능,스파가능,피부병치료
    @ResponseBody
    @GetMapping("/search/hashtags/top_reviews")
    public BaseResponse<List<StoreDTO>> searchHashTagsMostReviews(@RequestParam List<String> tags) {
        try {
            if (tags == null || tags.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchTagsMostReviews(tags);

            String hashtags = String.join(",", tags);
            searchService.postSearchHistory(user, hashtags);
            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);
            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //해시태그- 후기별점 평균 순
    // GET /search/hashtags/avg?tags=픽업가능,스파가능,피부병치료
    @ResponseBody
    @GetMapping("/search/hashtags/avg")
    public BaseResponse<List<StoreDTO>> searchReviewsAvgByHashtags(@RequestParam List<String> tags) {
        try {
            if (tags == null || tags.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.searchTagsReviewsAvg(tags);

            String hashtags = String.join(",", tags);
            searchService.postSearchHistory(user, hashtags);
            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);
            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //해시태그- 거리순
    // GET /search/hashtags/recommend?tags=픽업가능,스파가능,피부병치료
    @ResponseBody
    @GetMapping("/search/hashtags/recommend")
    public BaseResponse<List<StoreDTO>> searchByHashTagsRecommend(@RequestParam List<String> tags) {
        try {
            if (tags == null || tags.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principalDetails.getUser().getId();
            User user = userRepository.findById(userId);
            List<Store> store = searchService.recommendNearestHashTags(tags);

            String hashtags = String.join(",", tags);
            searchService.postSearchHistory(user, hashtags);
            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }
            List<StoreDTO> resultStore = convertStoreToDTO(store);
            return new BaseResponse<>(resultStore);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}

