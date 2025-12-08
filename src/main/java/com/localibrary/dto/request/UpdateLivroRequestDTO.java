// ==========================================
// 2. UpdateLivroRequestDTO.java
// DTO para PATCH /bibliotecas/{id}/livros/{id_livro}
// ==========================================
package com.localibrary.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

import static com.localibrary.util.Constants.MSG_ISBN_INVALIDO;
import static com.localibrary.util.Constants.REGEX_ISBN;

/**
 * ? NOVO: DTO para atualizar livro completo
 * Permite atualizar todos os campos edit�veis
 */
@Data
public class UpdateLivroRequestDTO {

    @Pattern(regexp = REGEX_ISBN, message = MSG_ISBN_INVALIDO)
    private String isbn;

    @NotBlank(message = "T�tulo � obrigat�rio")
    private String titulo;

    @NotBlank(message = "Autor � obrigat�rio")
    private String autor;

    private String editora;

    @Min(value = 1000, message = "Ano de publica��o inv�lido")
    private Integer anoPublicacao;

    private String capa;

    private String resumo;

    private String fotoAutor;

    @NotEmpty(message = "Pelo menos um g�nero deve ser selecionado")
    private Set<Long> generosIds;

    @NotNull(message = "Quantidade � obrigat�ria")
    @Min(value = 0, message = "Quantidade n�o pode ser negativa")
    private Integer quantidade;
}