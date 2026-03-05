package com.campusstudyhub.repository;

import com.campusstudyhub.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    /**
     * Find approved bookings that overlap with the given time range for a specific
     * room.
     * Two bookings overlap when one starts before the other ends AND ends after the
     * other starts.
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId "
            + "AND b.status = 'APPROVED' "
            + "AND b.startAt < :endAt "
            + "AND b.endAt > :startAt")
    List<Booking> findOverlappingApprovedBookings(
            @Param("roomId") Long roomId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    /**
     * Also check PENDING bookings for overlap to warn users.
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId "
            + "AND b.status IN ('APPROVED', 'PENDING') "
            + "AND b.startAt < :endAt "
            + "AND b.endAt > :startAt")
    List<Booking> findOverlappingActiveBookings(
            @Param("roomId") Long roomId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    List<Booking> findByStatus(String status);
}
