package com.localibrary.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateLivroRequestDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    private String editora;

    private Integer anoPublicacao;

    private String capa;

    private String resumo;

    @NotEmpty
    private Set<Long> generosIds;
}
