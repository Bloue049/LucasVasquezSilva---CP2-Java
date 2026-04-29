package com.meetingroom.api.service;

import com.meetingroom.api.dto.RoomDTO;
import com.meetingroom.api.exception.ResourceNotFoundException;
import com.meetingroom.api.model.Room;
import com.meetingroom.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDTO create(RoomDTO dto) {
        log.info("Criando sala: {}", dto.getNome());
        Room room = toEntity(dto);
        Room saved = roomRepository.save(room);
        log.info("Sala criada com ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Cacheable(value = "rooms")
    public Page<RoomDTO> findAll(String nome, String localizacao, Integer capacidadeMin, Pageable pageable) {
        log.debug("Listando salas com filtros - nome: {}, localizacao: {}, capacidadeMin: {}", nome, localizacao, capacidadeMin);
        return roomRepository.findWithFilters(nome, localizacao, capacidadeMin, pageable)
                .map(this::toDTO);
    }

    @Cacheable(value = "room", key = "#id")
    public RoomDTO findById(Long id) {
        log.debug("Buscando sala por ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
        return toDTO(room);
    }

    @Transactional
    @CacheEvict(value = {"rooms", "room"}, allEntries = true)
    public RoomDTO update(Long id, RoomDTO dto) {
        log.info("Atualizando sala ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
        room.setNome(dto.getNome());
        room.setCapacidade(dto.getCapacidade());
        room.setLocalizacao(dto.getLocalizacao());
        Room updated = roomRepository.save(room);
        log.info("Sala ID {} atualizada com sucesso", id);
        return toDTO(updated);
    }

    @Transactional
    @CacheEvict(value = {"rooms", "room"}, allEntries = true)
    public void delete(Long id) {
        log.info("Removendo sala ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
        roomRepository.delete(room);
        log.info("Sala ID {} removida com sucesso", id);
    }

    // Mapper manual (sem MapStruct para simplicidade)
    private RoomDTO toDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .nome(room.getNome())
                .capacidade(room.getCapacidade())
                .localizacao(room.getLocalizacao())
                .build();
    }

    private Room toEntity(RoomDTO dto) {
        return Room.builder()
                .nome(dto.getNome())
                .capacidade(dto.getCapacidade())
                .localizacao(dto.getLocalizacao())
                .build();
    }

    // Método interno para uso no ReservationService
    public Room findEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com ID: " + id));
    }
}
