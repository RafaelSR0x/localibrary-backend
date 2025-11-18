package com.localibrary.dto;

import com.localibrary.entity.Endereco;
import lombok.Data;

@Data
public class EnderecoDTO {
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;

    public EnderecoDTO(Endereco e) {
        this.cep = e.getCep();
        this.logradouro = e.getLogradouro();
        this.numero = e.getNumero();
        this.complemento = e.getComplemento();
        this.bairro = e.getBairro();
        this.cidade = e.getCidade();
        this.estado = e.getEstado();
    }
}