package com.localibrary.dto.request;

import com.localibrary.util.Constants;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para requisição de adição de livro ao acervo.
 *
 * RF-11: Adicionar livro ao acervo da biblioteca
 * RN-05: Todos os campos obrigatórios devem estar preenchidos
 * RN-16: Livro pode ter múltiplos gêneros
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroRequestDTO {

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Size(max = 255, message = "Autor deve ter no máximo 255 caracteres")
    private String autor;

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Pattern(regexp = "\\d{13}", message = Constants.MSG_ISBN_INVALIDO)
    private String isbn;

    @Size(max = 100, message = "Editora deve ter no máximo 100 caracteres")
    private String editora;

    @NotNull(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Min(value = 1000, message = "Ano de publicação deve ser maior que 1000")
    @Max(value = 2100, message = "Ano de publicação deve ser menor que 2100")
    private Integer anoPublicacao;

    @Size(max = 255, message = "URL da capa deve ter no máximo 255 caracteres")
    @Pattern(regexp = "^(https?://).*", message = "URL da capa deve começar com http:// ou https://")
    private String capa;

    @Size(max = 5000, message = "Resumo deve ter no máximo 5000 caracteres")
    private String resumo;

    @Size(max = 255, message = "URL da foto do autor deve ter no máximo 255 caracteres")
    @Pattern(regexp = "^(https?://).*", message = "URL da foto deve começar com http:// ou https://")
    private String fotoAutor;

    @NotEmpty(message = "Livro deve ter pelo menos um gênero")
    @Size(min = 1, max = 10, message = "Livro deve ter entre 1 e 10 gêneros")
    private List<Long> generosIds;

    @NotNull(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    @Max(value = 1000, message = "Quantidade deve ser no máximo 1000")
    private Integer quantidade;
}