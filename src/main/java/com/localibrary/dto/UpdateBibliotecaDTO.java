package com.localibrary.dto;

import com.localibrary.enums.CategoriaBiblioteca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBibliotecaDTO {

    @NotBlank
    private String nomeFantasia;
    @NotBlank
    private String razaoSocial;
    private String telefone;
    @NotNull
    private CategoriaBiblioteca categoria;
    private String site;
    private String fotoBiblioteca;

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