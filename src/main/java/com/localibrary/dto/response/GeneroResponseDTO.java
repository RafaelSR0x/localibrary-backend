package com.localibrary.dto.response;

import com.localibrary.entity.Genero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Gênero literário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneroResponseDTO {

    private Long id;
    private String nome;

    /**
     * Construtor a partir da entidade Genero
     */
    public GeneroResponseDTO(Genero genero) {
        this.id = genero.getId();
        this.nome = genero.getNomeGenero();
    }
}