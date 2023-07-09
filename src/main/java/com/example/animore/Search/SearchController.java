package com.example.animore.Search;

import com.example.Config.BaseException;
import com.example.Config.BaseResponse;
import com.example.animore.Search.model.SearchHistory;
import com.example.animore.Search.model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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


    //최근 검색어 API
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<SearchHistory>> searchHistory() {
        try {
            //int userIdx = jwtService.getUserIdx();
            int userIdx = 1; //임시지정
            List<SearchHistory> searchHistory = searchService.searchHistory(userIdx);
            return new BaseResponse<>(searchHistory);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //가게이름 검색 API
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


    //주소 검색 API
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

}
