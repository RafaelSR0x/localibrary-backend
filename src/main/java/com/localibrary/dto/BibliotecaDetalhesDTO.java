package com.localibrary.dto;

import com.localibrary.entity.Biblioteca;
import lombok.Data;

@Data
public class BibliotecaDetalhesDTO {
    private Long id;
    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private String telefone;
    private String categoria;
    private String site;
    private String fotoBiblioteca;
    private EnderecoDTO endereco;

    public BibliotecaDetalhesDTO(Biblioteca b) {
        this.id = b.getId();
        this.nomeFantasia = b.getNomeFantasia();
        this.razaoSocial = b.getRazaoSocial();
        this.cnpj = b.getCnpj();
        this.telefone = b.getTelefone();
        this.categoria = b.getCategoria().name(); // Pega o nome do Enum
        this.site = b.getSite();
        this.fotoBiblioteca = b.getFotoBiblioteca();
        if (b.getEndereco() != null) {
            this.endereco = new EnderecoDTO(b.getEndereco());
        }
    }
}