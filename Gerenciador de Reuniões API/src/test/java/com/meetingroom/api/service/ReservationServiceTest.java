package com.meetingroom.api.service;

import com.meetingroom.api.dto.ReservationDTO;
import com.meetingroom.api.exception.ReservationConflictException;
import com.meetingroom.api.exception.ResourceNotFoundException;
import com.meetingroom.api.model.Reservation;
import com.meetingroom.api.model.Room;
import com.meetingroom.api.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da ReservationService")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationService reservationService;

    private Room roomSample;
    private Reservation reservationSample;
    private ReservationDTO dtoSample;

    private final LocalDateTime INICIO = LocalDateTime.now().plusDays(1);
    private final LocalDateTime FIM = LocalDateTime.now().plusDays(1).plusHours(2);

    @BeforeEach
    void setUp() {
        roomSample = Room.builder()
                .id(1L)
                .nome("Sala Alpha")
                .capacidade(10)
                .localizacao("Andar 1")
                .build();

        reservationSample = Reservation.builder()
                .id(1L)
                .room(roomSample)
                .dataHoraInicio(INICIO)
                .dataHoraFim(FIM)
                .responsavel("João Silva")
                .status(Reservation.ReservationStatus.ATIVA)
                .build();

        dtoSample = ReservationDTO.builder()
                .salaId(1L)
                .dataHoraInicio(INICIO)
                .dataHoraFim(FIM)
                .responsavel("João Silva")
                .build();
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso quando não há conflito")
    void deveCriarReservaSemConflito() {
        when(roomService.findEntityById(1L)).thenReturn(roomSample);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservationSample);

        ReservationDTO resultado = reservationService.create(dtoSample);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getResponsavel()).isEqualTo("João Silva");
        assertThat(resultado.getStatus()).isEqualTo("ATIVA");
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando há conflito de horário")
    void deveLancarExcecaoComConflito() {
        when(roomService.findEntityById(1L)).thenReturn(roomSample);
        when(reservationRepository.findConflictingReservations(anyLong(), any(), any()))
                .thenReturn(List.of(reservationSample));

        assertThatThrownBy(() -> reservationService.create(dtoSample))
                .isInstanceOf(ReservationConflictException.class)
                .hasMessageContaining("Conflito de horário");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data fim é antes do inicio")
    void deveLancarExcecaoDataFimInvalida() {
        dtoSample.setDataHoraFim(INICIO.minusHours(1)); // fim antes do início

        assertThatThrownBy(() -> reservationService.create(dtoSample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fim deve ser posterior");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve cancelar reserva ativa com sucesso")
    void deveCancelarReservaAtiva() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservationSample));
        reservationSample.setStatus(Reservation.ReservationStatus.ATIVA);

        Reservation cancelada = Reservation.builder()
                .id(1L)
                .room(roomSample)
                .dataHoraInicio(INICIO)
                .dataHoraFim(FIM)
                .responsavel("João Silva")
                .status(Reservation.ReservationStatus.CANCELADA)
                .build();

        when(reservationRepository.save(any())).thenReturn(cancelada);

        ReservationDTO resultado = reservationService.cancel(1L);

        assertThat(resultado.getStatus()).isEqualTo("CANCELADA");
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar reserva inexistente")
    void deveLancarExcecaoAoCancelarInexistente() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.cancel(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
