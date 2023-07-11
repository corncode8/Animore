package com.example.animore.Search;


import com.example.animore.Search.model.Store;
import com.example.animore.Search.model.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {

    Town getTownIdByCityAndDistrict(String city, String district); //town_id찾기
}
