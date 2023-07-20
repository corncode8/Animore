package umc.animore.service;

import org.springframework.stereotype.Service;
import umc.animore.repository.StoreRepository;

@Service
public class StoreService {
    private StoreRepository storeRepository;
    //private ReviewRepository reviewRepository;

    public StoreService(StoreRepository storeRepository){
        this.storeRepository = storeRepository;
        //this.reviewRepository = reviewReopsitory;
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

