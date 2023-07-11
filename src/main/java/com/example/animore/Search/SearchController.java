package com.example.animore.Search;

import com.example.Config.BaseException;
import com.example.Config.BaseResponse;
import com.example.animore.Search.model.SearchHistory;
import com.example.animore.Search.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Config.BaseResponseStatus.*;

@RestController
public class SearchController {

    @Autowired
    private final SearchService searchService;

    //@Autowired
    //private final JwtService jwtService;

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
            int userIdx = 1; //임시지정
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
            int userIdx = 1; //임시지정
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

    //시도 검색 API - 검색화면
    @ResponseBody
    @GetMapping("/search/test")
    public BaseResponse<List<Store>> searchByTown(@RequestParam("city") String city, @RequestParam("district") String district) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            int userIdx = 1; // 임시지정
            List<Store> store = searchService.searchCityList(city,district);
            //searchService.postSearchHistory(userIdx, city + " " + district);

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

//    //후기많은순 API (3개씩) - 메인화면
//    //GET /search/top3
//    @ResponseBody
//    @GetMapping("/search/top3")
//    public BaseResponse<List<Store>> searchTopreview() {
//        try {
//            //int userIdx = jwtService.getUserIdx();
//            int userIdx = 1; //임시지정
//            List<Store> store = searchService.findStoresWithMostReviews();
//            return new BaseResponse<>(store);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }


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




//    //가게이름 검색 API - 검색화면 (페이징)
//    //GET /search/storename?query=가게이름&page=2
//    @ResponseBody
//    @GetMapping("/search/storename")
//    public BaseResponse<Page<Store>> searchName(@RequestParam(value = "query") String storeName, @RequestParam(defaultValue = "0") int page) {
//        try {
//
//            if (storeName == null || storeName.equals("")) {
//                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
//            }
//            if (storeName.length() > 20) {
//                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
//            }
//
//            //int userIdx = jwtService.getUserIdx();
//            int userIdx = 1; //임시지정
//            Page<Store> store = searchService.searchNameListTopPage(storeName, page);
//            searchService.postSearchHistory(userIdx, storeName);
//
//            System.out.println("query: " + storeName);
//            System.out.println("가게정보: " + store);
//
//            if (store.isEmpty()) {
//                // 반환값이 없다
//                return new BaseResponse<>(DATABASE_ERROR);
//            }
//
//            return new BaseResponse<>(store);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }





//    //주소 검색 API - 검색화면 (페이징)
//    //GET /search/storelocation?query=가게주소&page=2
//    @ResponseBody
//    @GetMapping("/search/storelocation")
//    public BaseResponse<Page<Store>> searchLocation (@RequestParam(value = "query") String storeLocation,@RequestParam(defaultValue = "0") int page ) {
//        try {
//            if (storeLocation == null || storeLocation.equals("")) {
//                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
//            }
//            if (storeLocation.length() > 100) {
//                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY2);
//            }
//
//            //int userIdx = jwtService.getUserIdx();
//            int userIdx = 1; //임시지정
//            Page<Store> store = searchService.searchLocationListTopPage(storeLocation,page);
//            searchService.postSearchHistory(userIdx, storeLocation);
//
//            System.out.println("query: " + storeLocation);
//            System.out.println("가게정보: " + store);
//
//            if (store.isEmpty()) {
//                // 반환값이 없다
//                return new BaseResponse<>(DATABASE_ERROR);
//            }
//
//            return new BaseResponse<>(store);
//
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//
//    }
    /*

    //시도 검색 API - 검색화면 (페이징)
    //GET /search/storetown?townName=서울시&city=강남구
    @ResponseBody
    @GetMapping("/search/storetown")
    public BaseResponse<Page<Store>> searchByTownAndCity(@RequestParam("city") String city, @RequestParam("district") String district, @RequestParam(defaultValue = "0") int page) {
        try {
            if (district == null || district.equals("") || city == null || city.equals("")) {
                return new BaseResponse<>(GET_SEARCH_EMPTY_QUERY);
            }
            if (district.length() > 50 || city.length() > 50) {
                return new BaseResponse<>(GET_SEARCH_INVALID_QUERY1);
            }

            int userIdx = 1; // 임시지정
            Page<Store> store = searchService.searchStoresByCityAndDistrict(city,district, page);
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

     */






}

