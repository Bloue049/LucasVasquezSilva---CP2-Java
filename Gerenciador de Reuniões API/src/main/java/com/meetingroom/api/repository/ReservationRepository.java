package com.meetingroom.api.repository;

import com.meetingroom.api.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAll(Pageable pageable);

    // Verifica conflito de horário para uma sala
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND r.status = 'ATIVA' " +
           "AND r.dataHoraInicio < :fim " +
           "AND r.dataHoraFim > :inicio")
    List<Reservation> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Verifica conflito excluindo a própria reserva (para update)
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND r.id != :reservationId " +
           "AND r.status = 'ATIVA' " +
           "AND r.dataHoraInicio < :fim " +
           "AND r.dataHoraFim > :inicio")
    List<Reservation> findConflictingReservationsExcluding(
            @Param("roomId") Long roomId,
            @Param("reservationId") Long reservationId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Filtros
    @Query("SELECT r FROM Reservation r WHERE " +
           "(:roomId IS NULL OR r.room.id = :roomId) AND " +
           "(:responsavel IS NULL OR LOWER(r.responsavel) LIKE LOWER(CONCAT('%', :responsavel, '%'))) AND " +
           "(:inicio IS NULL OR r.dataHoraInicio >= :inicio) AND " +
           "(:fim IS NULL OR r.dataHoraFim <= :fim) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Reservation> findWithFilters(
            @Param("roomId") Long roomId,
            @Param("responsavel") String responsavel,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") Reservation.ReservationStatus status,
            Pageable pageable);
}
