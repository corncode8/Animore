package com.example.animore.Search;

import com.example.animore.Search.model.Store;
import com.example.Config.BaseException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.Config.BaseResponseStatus.RESPONSE_ERROR;

@Service
public class StoreService {
    private StoreRepository storeRepository;
    private ReviewRepository reviewRepository;

    public StoreService(StoreRepository storeRepository, ReviewRepository reviewRepository){
        this.storeRepository = storeRepository;
        this.reviewRepository = reviewRepository;
    }

//    public List<Store> addReviewToStore(int storeIdx) throws BaseException {
//        try {
//            List<Store> store = storeRepository.findByStoreIdx(storeIdx);
//            return store;
//        }catch (Exception exception) {
//            throw new BaseException(RESPONSE_ERROR);
//        }
//    }



}

