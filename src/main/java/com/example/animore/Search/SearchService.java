package com.example.animore.Search;

import com.example.Config.BaseException;
import com.example.animore.Search.model.SearchHistory;
import com.example.animore.Search.model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.Config.BaseResponseStatus.*;

@Service
public class SearchService {

    private SearchRespository searchRespository;

    private SearchHistoryRepository searchHistoryRepository;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired SearchService(SearchRespository searchRespository, SearchHistoryRepository searchHistoryRepository){
        this.searchRespository = searchRespository;
        this.searchHistoryRepository = searchHistoryRepository;
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


    //주소
    public List<Store> searchLocationList(String storeLocation) throws BaseException {
        try{
            List<Store> store = searchRespository.findByStoreLocationContaining(storeLocation);
            return store;
        }catch (Exception exception){
            throw new BaseException(RESPONSE_ERROR);
        }
    }



    public List<SearchHistory> searchHistory(int userIdx) throws BaseException {
        try {
            List<SearchHistory> searchHistoryRes = searchHistoryRepository.findByUserIdxOrderBySearchCreateAtDesc(userIdx);
            return searchHistoryRes;
        }catch (Exception exception){
            throw  new BaseException(RESPONSE_ERROR);
        }
    }

    public void postSearchHistory(int userIdx, String searchQuery) {
        List<SearchHistory> searchHistoryList = searchHistoryRepository.findByUserIdxOrderBySearchCreateAtDesc(userIdx);

        if (searchHistoryList.size() < 4) {
            saveQuery(userIdx, searchQuery);
        } else {
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
