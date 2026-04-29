package com.meetingroom.api.service;

import com.meetingroom.api.dto.RoomDTO;
import com.meetingroom.api.exception.ResourceNotFoundException;
import com.meetingroom.api.model.Room;
import com.meetingroom.api.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da RoomService")
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room roomSample;
    private RoomDTO roomDTOSample;

    @BeforeEach
    void setUp() {
        roomSample = Room.builder()
                .id(1L)
                .nome("Sala Teste")
                .capacidade(10)
                .localizacao("Andar 1")
                .build();

        roomDTOSample = RoomDTO.builder()
                .nome("Sala Teste")
                .capacidade(10)
                .localizacao("Andar 1")
                .build();
    }

    @Test
    @DisplayName("Deve criar sala com sucesso")
    void deveCriarSalaComSucesso() {
        when(roomRepository.save(any(Room.class))).thenReturn(roomSample);

        RoomDTO resultado = roomService.create(roomDTOSample);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Sala Teste");
        assertThat(resultado.getCapacidade()).isEqualTo(10);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar sala inexistente")
    void deveLancarExcecaoSalaInexistente() {
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(roomRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve atualizar sala com sucesso")
    void deveAtualizarSalaComSucesso() {
        RoomDTO updateDTO = RoomDTO.builder()
                .nome("Sala Atualizada")
                .capacidade(20)
                .localizacao("Andar 2")
                .build();

        Room updated = Room.builder()
                .id(1L)
                .nome("Sala Atualizada")
                .capacidade(20)
                .localizacao("Andar 2")
                .build();

        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomSample));
        when(roomRepository.save(any(Room.class))).thenReturn(updated);

        RoomDTO resultado = roomService.update(1L, updateDTO);

        assertThat(resultado.getNome()).isEqualTo("Sala Atualizada");
        assertThat(resultado.getCapacidade()).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve deletar sala com sucesso")
    void deveDeletarSalaComSucesso() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(roomSample));
        doNothing().when(roomRepository).delete(roomSample);

        assertThatCode(() -> roomService.delete(1L)).doesNotThrowAnyException();

        verify(roomRepository).delete(roomSample);
    }
}
