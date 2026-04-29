package com.meetingroom.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Min(value = 1, message = "Capacidade deve ser pelo menos 1")
    @Column(nullable = false)
    private Integer capacidade;

    @NotBlank(message = "Localização é obrigatória")
    @Column(nullable = false)
    private String localizacao;
}
