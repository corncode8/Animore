package umc.animore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import umc.animore.config.exception.BaseException;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypageStoreUpdate;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.model.review.StoreDTO;
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
    public MypageStoreUpdate saveMypageStoreUpdate(MypageStoreUpdate mypageStoreUpdate, @PathVariable Long storeId) throws BaseException {

        try {
            if (mypageStoreUpdate.getStoreName() == null) {
                throw new BaseException(GET_USER_EMPTY_NICKNAME_NAME);
            }


            Store store = storeRepository.findByStoreId(storeId);

            store.setStoreName(mypageStoreUpdate.getStoreName());
            store.setStoreImageUrl(mypageStoreUpdate.getStoreImageUrl());
            store.setStoreExplain(mypageStoreUpdate.getStoreExplain());
            store.setOpen(TimeCalculation(mypageStoreUpdate.getOpen()));
            store.setClose(TimeCalculation(mypageStoreUpdate.getClose()));
            store.setDayoff1(DateToEng(mypageStoreUpdate.getDayoff1()));
            store.setDayoff2(DateToEng(mypageStoreUpdate.getDayoff2()));
            store.setStoreSignificant(DateToEng(mypageStoreUpdate.getStoreSignificant()));
            store.setAmount(AmountStringToInt(mypageStoreUpdate.getAmount()));
            store.setTags(mypageStoreUpdate.getTags());
            storeRepository.save(store);

            return mypageStoreUpdate;
        }
        catch(Exception e){
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    //최대 예약 건수 int 변환
    public int AmountStringToInt(String amountString) {
        amountString = amountString.replaceAll("[^0-9]", ""); // 정규식을 사용하여 숫자 이외의 문자 제거
        int amount = Integer.parseInt(amountString);
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
        DateMapping.put("월요일", "MONDAY");
        DateMapping.put("화요일", "TUESDAY");
        DateMapping.put("수요일", "WEDNESDAY");
        DateMapping.put("목요일", "THURSDAY");
        DateMapping.put("금요일", "FRIDAY");
        DateMapping.put("토요일", "SATURDAY");
        DateMapping.put("일요일", "SUNDAY");

        return DateMapping.get(day);
    }
}

