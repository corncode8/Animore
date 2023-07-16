package com.example.animore.Search;

import com.example.animore.Search.model.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {

    //최근 검색기록 - 3개
    List<SearchHistory> findByUserIdxOrderBySearchCreateAtDesc(int userIdx);
}
