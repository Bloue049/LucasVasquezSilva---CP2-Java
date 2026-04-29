package com.meetingroom.api.service;

import com.meetingroom.api.dto.ReservationDTO;
import com.meetingroom.api.exception.ReservationConflictException;
import com.meetingroom.api.exception.ResourceNotFoundException;
import com.meetingroom.api.model.Reservation;
import com.meetingroom.api.model.Room;
import com.meetingroom.api.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    @Transactional
    public ReservationDTO create(ReservationDTO dto) {
        log.info("Criando reserva para sala ID {} por {}", dto.getSalaId(), dto.getResponsavel());

        validateDateRange(dto.getDataHoraInicio(), dto.getDataHoraFim());

        Room room = roomService.findEntityById(dto.getSalaId());

        checkConflict(dto.getSalaId(), dto.getDataHoraInicio(), dto.getDataHoraFim(), null);

        Reservation reservation = Reservation.builder()
                .room(room)
                .dataHoraInicio(dto.getDataHoraInicio())
                .dataHoraFim(dto.getDataHoraFim())
                .responsavel(dto.getResponsavel())
                .status(Reservation.ReservationStatus.ATIVA)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reserva criada com ID: {}", saved.getId());
        return toDTO(saved);
    }

    public Page<ReservationDTO> findAll(Long roomId,
                                        String responsavel,
                                        LocalDateTime inicio,
                                        LocalDateTime fim,
                                        String status,
                                        Pageable pageable) {
        log.debug("Listando reservas com filtros");
        Reservation.ReservationStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = Reservation.ReservationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido. Use: ATIVA ou CANCELADA");
            }
        }
        return reservationRepository
                .findWithFilters(roomId, responsavel, inicio, fim, statusEnum, pageable)
                .map(this::toDTO);
    }

    public ReservationDTO findById(Long id) {
        log.debug("Buscando reserva ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
        return toDTO(reservation);
    }

    @Transactional
    public ReservationDTO cancel(Long id) {
        log.info("Cancelando reserva ID: {}", id);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELADA) {
            throw new IllegalArgumentException("Reserva já está cancelada");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELADA);
        Reservation updated = reservationRepository.save(reservation);
        log.info("Reserva ID {} cancelada com sucesso", id);
        return toDTO(updated);
    }

    // ========================
    // Regras de negócio
    // ========================

    private void validateDateRange(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("A data/hora de fim deve ser posterior à data/hora de início");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível criar reservas no passado");
        }
    }

    private void checkConflict(Long roomId, LocalDateTime inicio, LocalDateTime fim, Long excludeId) {
        List<Reservation> conflicts;
        if (excludeId != null) {
            conflicts = reservationRepository.findConflictingReservationsExcluding(roomId, excludeId, inicio, fim);
        } else {
            conflicts = reservationRepository.findConflictingReservations(roomId, inicio, fim);
        }
        if (!conflicts.isEmpty()) {
            log.warn("Conflito detectado para sala ID {} entre {} e {}", roomId, inicio, fim);
            throw new ReservationConflictException(
                    String.format("Conflito de horário! Já existe reserva para esta sala entre %s e %s",
                            conflicts.get(0).getDataHoraInicio(), conflicts.get(0).getDataHoraFim())
            );
        }
    }

    private ReservationDTO toDTO(Reservation r) {
        return ReservationDTO.builder()
                .id(r.getId())
                .salaId(r.getRoom().getId())
                .salaNome(r.getRoom().getNome())
                .dataHoraInicio(r.getDataHoraInicio())
                .dataHoraFim(r.getDataHoraFim())
                .responsavel(r.getResponsavel())
                .status(r.getStatus().name())
                .build();
    }
}
