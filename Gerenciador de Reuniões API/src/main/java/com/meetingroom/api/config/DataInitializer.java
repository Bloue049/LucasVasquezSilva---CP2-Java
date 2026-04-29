package com.meetingroom.api.config;

import com.meetingroom.api.model.Room;
import com.meetingroom.api.model.User;
import com.meetingroom.api.repository.RoomRepository;
import com.meetingroom.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build());
            log.info("Usuário admin criado com sucesso");
        }

        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder().nome("Sala Alpha").capacidade(10).localizacao("Andar 1 - Bloco A").build());
            roomRepository.save(Room.builder().nome("Sala Beta").capacidade(20).localizacao("Andar 2 - Bloco B").build());
            roomRepository.save(Room.builder().nome("Auditório Central").capacidade(100).localizacao("Térreo - Bloco C").build());
            log.info("Salas de exemplo criadas com sucesso");
        }
    }
}