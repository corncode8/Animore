package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.animore.config.exception.BaseException;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypageStoreUpdate;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.repository.StoreRepository;

import java.util.HashMap;

import static umc.animore.config.exception.BaseResponseStatus.*;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;


    public StoreService(StoreRepository storeRepository){
        this.storeRepository = storeRepository;

    }

    public Store findStoreId(Long storeId) {
        return storeRepository.findByStoreId(storeId);
    }

    //가게 정보 수정
    @Transactional
    public MypageStoreUpdate saveMypageStoreUpdate(MypageStoreUpdate mypageStoreUpdate, Long storeId) throws BaseException {

        try {
            if (mypageStoreUpdate.getStoreName() == null) {
                throw new BaseException(GET_USER_EMPTY_NICKNAME_NAME);
            }


            Store store = storeRepository.findByStoreId(storeId);

            store.setStoreName(mypageStoreUpdate.getStoreName());
            store.setStoreExplain(mypageStoreUpdate.getStoreExplain());
            store.setOpen(TimeCalculation(mypageStoreUpdate.getOpen()));
            store.setClose(TimeCalculation(mypageStoreUpdate.getClose()));
            store.setDayoff1(DateToEng(mypageStoreUpdate.getDayoff1()));
            store.setDayoff2(DateToEng(mypageStoreUpdate.getDayoff2()));
            store.setAmount(AmountStringToInt(mypageStoreUpdate.getAmount()));
            store.setStoreSignificant(mypageStoreUpdate.getStoreSignificant());
            store.setTag(mypageStoreUpdate.getTag());
            storeRepository.save(store);

            return mypageStoreUpdate;
        }
        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //최대 예약 건수 int 변환
    public int AmountStringToInt(String Amount){
        Amount.substring(4);
        Amount.substring(0,-1);
        System.out.println(Amount);
        int amount = Integer.parseInt(Amount);
        return amount;
    }

    //Time String -> int 변환
    public int TimeCalculation(String Time){
        int time = 0;
        String[] Hour = Time.split(":");
        time += Integer.parseInt(Hour[0]) * 60;
        time += Integer.parseInt(Hour[1]);
        return time;
    }

    //휴무일 영문 변환
    private String DateToEng(String day){
        HashMap<String, String> DateMapping = new HashMap<String, String>();
        DateMapping.put("월요일", "Mon");
        DateMapping.put("화요일", "Ths");
        DateMapping.put("수요일", "Wen");
        DateMapping.put("목요일", "Thr");
        DateMapping.put("금요일", "Fir");
        DateMapping.put("토요일", "Sat");
        DateMapping.put("일요일", "Sun");

        return DateMapping.get(day);
    }
}

