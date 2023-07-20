package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.SearchHistory;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Integer> {

    //최근 검색기록 - 3개
    List<SearchHistory> findByUserIdxOrderBySearchCreateAtDesc(int userIdx);
}
