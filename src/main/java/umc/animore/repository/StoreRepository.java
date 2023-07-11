package umc.animore.repository;

import umc.animore.model.Store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
        Page<Store> findAllByIsDiscountedTrue(Pageable pageable);

}
