package umc.animore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import umc.animore.config.auth.PrincipalDetails;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;
import umc.animore.controller.DTO.MypageMemberUpdate;
import umc.animore.controller.DTO.MypageStoreUpdate;
import umc.animore.service.StoreService;

@RestController
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("/manage/store")
    public BaseResponse<MypageStoreUpdate> UpdateStore(@RequestBody MypageStoreUpdate mypageStoreUpdate){

        try {
            PrincipalDetails principalDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long storeId = principalDetails.getUser().getStore().getStoreId();

            return new BaseResponse<>(storeService.saveMypageStoreUpdate(mypageStoreUpdate, storeId));

        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
