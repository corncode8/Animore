package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.SearchHistory;
import umc.animore.model.User;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    //최근 검색기록 - 3개
    List<SearchHistory> findByUserOrderBySearchCreateAtDesc(User user);
}
