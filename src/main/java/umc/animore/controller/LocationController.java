package umc.animore.controller;


import umc.animore.config.exception.BaseResponse;
import umc.animore.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.animore.repository.LocationRepository;

import java.util.Optional;

import static umc.animore.config.exception.BaseResponseStatus.EMPTY_PET_ID;

@RestController
@RequestMapping
public class LocationController {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    //현재 위치 위도 경도 클라이언트에서 받아온거 저장
    @PatchMapping("/locations/{locationId}")
    public BaseResponse<Location> saveLocation(@PathVariable Long locationId,@RequestParam Double latitude, @RequestParam Double longitude) {

        Optional<Location> optionalLocation = locationRepository.findById(locationId);

        Location nowLocation = optionalLocation.get();
        nowLocation.setLatitude(latitude);
        nowLocation.setLongitude(longitude);
        nowLocation = locationRepository.save(nowLocation);

        return new BaseResponse<>(true,"현재 위치 저장 성공",1000,nowLocation);

    }


    @GetMapping("/locations/{locationId}")
    public BaseResponse<Location> getLocation(@PathVariable Long locationId){

            Location nowLocation = locationRepository.findByLocationId(locationId);

            return new BaseResponse<>(true, "현재 위치 조회 성공", 1000, nowLocation);
    }


}