package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.model.Store;
import umc.animore.service.SearchService;

import java.util.List;

import static umc.animore.config.exception.BaseResponseStatus.*;


@RestController
public class SearchController {


    private final SearchService searchService;

    //@Autowired
    //private final JwtService jwtService;

    @Autowired
    public SearchController(SearchService searchService){
        this.searchService = searchService;
        //this.jwtService = jwtService;
    }


    //가게이름 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name")
    public BaseResponse<List<Store>> searchName(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchNameList(storeName);
            searchService.postSearchHistory(userIdx, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 인기순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/best")
    public BaseResponse<List<Store>> searchNameBest(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchNameBestList(storeName);
            searchService.postSearchHistory(userIdx, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    //가게이름 검색 후기많은순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/top_reviews")
    public BaseResponse<List<Store>> searchNameMostReviews(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchNameMostReviewsList(storeName);
            searchService.postSearchHistory(userIdx, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 후기 평점 평균 높은 순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/avg")
    public BaseResponse<List<Store>> searchNameReviewsAVG(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchNameReviewsAvgList(storeName);
            searchService.postSearchHistory(userIdx, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/name/recommend")
    public BaseResponse<List<Store>> searchNameRecommend(@RequestParam(value = "query") String storeName) {
        try {

            if (storeName == null || storeName.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeName.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.recommendNearestStore(storeName);
            searchService.postSearchHistory(userIdx, storeName);

            System.out.println("query: " + storeName);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //주소 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/location")
    public BaseResponse<List<Store>> searchLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchLocationList(storeLocation);
            searchService.postSearchHistory(userIdx, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //주소 검색 인기순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/best")
    public BaseResponse<List<Store>> searchBestLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchLocationBestList(storeLocation);
            searchService.postSearchHistory(userIdx, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    //주소 검색 후기 많은 순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/top_reviews")
    public BaseResponse<List<Store>> searchMostReviewsLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchLocationMostReviewsList(storeLocation);
            searchService.postSearchHistory(userIdx, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //주소 검색 후기 평점 평균 높은 순 API -검색화면
    @ResponseBody
    @GetMapping("/search/location/avg")
    public BaseResponse<List<Store>> searchReviewsAvgLocation (@RequestParam(value = "query") String storeLocation) {
        try {
            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 100) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchLocationReviewsAvgList(storeLocation);
            searchService.postSearchHistory(userIdx, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //가게주소 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/location/recommend")
    public BaseResponse<List<Store>> searchLocationRecommend(@RequestParam(value = "query") String storeLocation) {
        try {

            if (storeLocation == null || storeLocation.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (storeLocation.length() > 20) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.recommendNearestStoreLocation(storeLocation);
            searchService.postSearchHistory(userIdx, storeLocation);

            System.out.println("query: " + storeLocation);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                // 반환값이 없다
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //시도 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town")
    public BaseResponse<List<Store>> searchByTown(@RequestParam("city") String city, @RequestParam("district") String district) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchCityList(city,district);
            searchService.postSearchHistory(userIdx, city + " " + district);

            System.out.println("query: " + city + " " + district);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 인기순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/best")
    public BaseResponse<List<Store>> searchBestByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchCityListBest(city, district);
            searchService.postSearchHistory(userIdx, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 후기 많은 순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/top_reviews")
    public BaseResponse<List<Store>> searchMostReviewsByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchCityListMostReviews(city, district);
            searchService.postSearchHistory(userIdx, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //시도 후기 평점 평균 높은순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/avg")
    public BaseResponse<List<Store>> searchReviewsAvgByTown (@RequestParam("city") String city, @RequestParam("district") String district){
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchCityListReviewsAvg(city, district);
            searchService.postSearchHistory(userIdx, city + " " + district);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    //시도 검색 거리순 API - 검색화면
    @ResponseBody
    @GetMapping("/search/town/recommend")
    public BaseResponse<List<Store>> searchByTownRecommend(@RequestParam("city") String city, @RequestParam("district") String district) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.recommendNearestStoreTown(city,district);
            searchService.postSearchHistory(userIdx, city + " " + district);

            System.out.println("query: " + city + " " + district);
            System.out.println("가게정보: " + store);

            if (store.isEmpty()) {
                return new BaseResponse<>(DATABASE_ERROR);
            }

            return new BaseResponse<>(store);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //후기많은순 API  - 메인화면
    //GET /search/top_reviews
    @ResponseBody
    @GetMapping("/search/top_reviews")
    public BaseResponse<List<Store>> searchTopreview() {
        try {
            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.getStoresWithMostReviews();
            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //예약많은순 API  - 메인화면
    //GET /search/top_reservation
    @ResponseBody
    @GetMapping("/search/top_reservation")
    public BaseResponse<List<Store>> searchTopreservation() {
        try {
            //int userIdx = jwtService.getUserIdx();
            Long userIdx = 1L; //임시지정
            List<Store> store = searchService.searchReservationMost();
            return new BaseResponse<>(store);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


//    //최근 검색어 API (3개씩) - 메인화면
//    //GET /search/recent?page=1
//    @ResponseBody
//    @GetMapping("/search/recent")
//    public BaseResponse<Page<SearchHistory>> searchHistory(@RequestParam(defaultValue = "0") int page) {
//        try {
//            //int userIdx = jwtService.getUserIdx();
//            int userIdx = 1; //임시지정
//            Page<SearchHistory> searchHistory = searchService.searchHistory(userIdx,page);
//            return new BaseResponse<>(searchHistory);
//        } catch (BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

}

