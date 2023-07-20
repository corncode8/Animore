package umc.animore.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umc.animore.model.Review;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    //List<Object[]> countReviewsByStore();
}
