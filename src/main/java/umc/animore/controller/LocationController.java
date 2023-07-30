package umc.animore.controller;

import umc.animore.config.exception.BaseResponse;
import umc.animore.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.animore.repository.LocationRepository;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    //현재 위치 위도 경도 클라이언트에서 받아온거 저장
    @PostMapping
    public BaseResponse<Location> saveLocation(@RequestBody Location location) {

            Location nowLocation=locationRepository.save(location);

            return new BaseResponse<>(true,"현재 위치 저장 성공",1000,nowLocation);

    }


    @GetMapping("/{locationId}")
    public BaseResponse<Location> getLocation(@PathVariable Long locationId) {

        Location nowLocation=locationRepository.findByLocationId(locationId);

        return new BaseResponse<>(true,"현재 위치 조회 성공",1000,nowLocation);

    }
}
