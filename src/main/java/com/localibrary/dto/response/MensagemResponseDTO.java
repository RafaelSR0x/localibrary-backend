package com.localibrary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO genérico para mensagens de resposta.
 * Usado para operações que não retornam dados específicos (create, update, delete).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagemResponseDTO {

    private String mensagem;
    private LocalDateTime timestamp;

    /**
     * Construtor que adiciona timestamp automaticamente
     */
    public MensagemResponseDTO(String mensagem) {
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }
}