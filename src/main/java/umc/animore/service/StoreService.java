package umc.animore.service;

import org.springframework.stereotype.Service;
import umc.animore.model.Store;
import umc.animore.repository.StoreRepository;

@Service
public class StoreService {
    private StoreRepository storeRepository;


    public StoreService(StoreRepository storeRepository){
        this.storeRepository = storeRepository;

    }

    public Store getStoreId(Long storeId) {
        return storeRepository.findByStoreId(storeId);
    }


}

