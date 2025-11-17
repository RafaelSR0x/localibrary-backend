package com.localibrary.dto.request;

import com.localibrary.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para cadastro de moderador.
 *
 * RF-23: Cadastrar moderador
 * RN-04: Apenas ADMIN pode cadastrar moderadores
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroModeradorRequestDTO {

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    private String nome;

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Size(max = 50, message = "Sobrenome deve ter no máximo 50 caracteres")
    private String sobrenome;

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Email(message = Constants.MSG_EMAIL_INVALIDO)
    private String email;

    @NotBlank(message = Constants.MSG_CAMPO_OBRIGATORIO)
    @Size(min = Constants.MIN_SENHA_LENGTH,
            max = Constants.MAX_SENHA_LENGTH,
            message = "Senha deve ter entre " + Constants.MIN_SENHA_LENGTH +
                    " e " + Constants.MAX_SENHA_LENGTH + " caracteres")
    private String senha;
}