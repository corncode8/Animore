package umc.animore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.animore.model.Reservation;
import umc.animore.model.Store;
import umc.animore.model.User;
import umc.animore.repository.DTO.ReservationInfoMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //예약 목록을 조회 (겹치는 예약 확인)
    @Query("SELECT r FROM Reservation r WHERE (r.startTime BETWEEN :startTime AND :endTime) OR (:startTime < r.startTime AND :endTime > r.startTime)")
    List<Reservation> getOverlappingReservations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // 예약 현황 확인 (시간 범위에 포함되는 예약 조회 쿼리)
    @Query("SELECT r FROM Reservation r WHERE r.startTime >= :start AND r.endTime <= :end")
    List<Reservation> findReservationsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 예약 알람
    List<Reservation> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // store의 예약 목록
    List<Reservation> findByStoreAndStartTimeBetweenOrderByStartTime(Store store, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    Reservation findByReservationId(Long reservation_id);
    Page<Reservation> findByUserId(Long user_id, Pageable pageable);

    Page<Reservation> findByConfirmedAndStore(boolean confirmed, Store store, Pageable pageable);
    List<Reservation> findByStartTimeIsNull();

    Reservation findByUserAndStoreAndConfirmed (User user, Store store, Boolean Confirmed);
    List<Reservation> findByUserIdAndStartTimeIsNull(Long userId);
    Reservation findByReservationIdAndUserId(Long reservation_id, Long user_id);


    List<ReservationInfoMapping> findByUser_IdOrderByStartTimeDesc(Long userId);



//    List<Reservation> findByStartTimeGreaterThanEqualAndEndTimeLessThanEqual(LocalDateTime start, LocalDateTime end);

    //해당 가게의 예약횟수 조회
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.startTime >= :twoWeeksAgo GROUP BY r.store ORDER BY COUNT(r) DESC")
    Integer findStoreWithHighestReservationCount(@Param("twoWeeksAgo") LocalDateTime twoWeeksAgo);

}