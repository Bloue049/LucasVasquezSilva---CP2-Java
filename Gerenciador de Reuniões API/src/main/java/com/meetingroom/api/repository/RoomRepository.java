package com.meetingroom.api.repository;

import com.meetingroom.api.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Page<Room> findAll(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE " +
           "(:nome IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:localizacao IS NULL OR LOWER(r.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%'))) AND " +
           "(:capacidadeMin IS NULL OR r.capacidade >= :capacidadeMin)")
    Page<Room> findWithFilters(
            @Param("nome") String nome,
            @Param("localizacao") String localizacao,
            @Param("capacidadeMin") Integer capacidadeMin,
            Pageable pageable);
}
