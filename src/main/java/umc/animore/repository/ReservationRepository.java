package umc.animore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.animore.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    @Query("SELECT r FROM Reservation r WHERE (r.startTime BETWEEN :startTime AND :endTime) OR (:startTime < r.startTime AND :endTime > r.startTime)")
    List<Reservation> getOverlappingReservations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

//    @Query("SELECT r FROM Reservation r WHERE r.startTime >= :start AND r.endTime <= :end")
//    List<Reservation> findReservationsBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Reservation r WHERE r.startTime >= :start AND r.endTime <= :end")
    List<Reservation> findReservationsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

//    List<Reservation> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime start, LocalDateTime end);

}
