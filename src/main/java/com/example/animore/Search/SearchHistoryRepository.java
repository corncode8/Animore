package com.example.animore.Search;

import com.example.animore.Search.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {



    List<SearchHistory> findByUserIdxOrderBySearchCreateAtDesc(int userIdx);
}
