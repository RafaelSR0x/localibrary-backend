package com.localibrary.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class AddLivroRequestDTO {

    // Parte 1: Dados do Livro (tbl_livro_base)
    @NotBlank
    private String isbn; // O Service vai checar se já existe

    @NotBlank
    private String titulo;

    @NotBlank
    private String autor;

    private String editora;

    private Integer anoPublicacao;

    private String capa;

    private String resumo;

    // Parte 2: Gêneros (tbl_livro_genero)
    @NotEmpty // Deve ter pelo menos um gênero
    private Set<Long> generosIds; // Lista de IDs de gêneros (de tbl_genero)

    // Parte 3: Inventário (tbl_biblioteca_livro)
    @NotNull
    @Min(1)
    private int quantidade;
}