package com.localibrary.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

import static com.localibrary.util.Constants.MSG_ISBN_INVALIDO;
import static com.localibrary.util.Constants.REGEX_ISBN;

@Data
public class AddLivroRequestDTO {

    @Pattern(regexp = REGEX_ISBN, message = MSG_ISBN_INVALIDO)
    private String isbn;

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

    @NotNull
    @Min(1)
    private Integer quantidade;
}