package com.meetingroom.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade mínima é 1")
    private Integer capacidade;

    @NotBlank(message = "Localização é obrigatória")
    private String localizacao;
}
