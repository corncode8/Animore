package umc.animore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.SearchStore;
import umc.animore.model.User;

import java.util.List;

public interface SearchStoreRepository extends JpaRepository<SearchStore, Long> {
    List<SearchStore> findByUserOrderBySearchCreateAtDesc(User user);
}

