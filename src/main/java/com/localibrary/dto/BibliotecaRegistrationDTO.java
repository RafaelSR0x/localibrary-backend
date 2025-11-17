package com.localibrary.dto;

import com.localibrary.enums.CategoriaBiblioteca;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
public class BibliotecaRegistrationDTO {

    // Dados da Biblioteca (tbl_biblioteca)
    @NotBlank
    private String nomeFantasia;
    @NotBlank
    private String razaoSocial;
    @NotBlank
    // @CNPJ(message = "CNPJ inválido")
    private String cnpj;
    private String telefone;
    @NotNull
    private CategoriaBiblioteca categoria;
    private String site;

    // Dados das Credenciais (tbl_credenciais_biblioteca)
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6)
    private String senha;

    // Dados do Endereço (tbl_endereco)
    @NotBlank
    private String cep;
    @NotBlank
    private String logradouro;
    @NotBlank
    private String numero;
    private String complemento;
    @NotBlank
    private String bairro;
    @NotBlank
    private String cidade;
    @NotBlank
    private String estado;
}