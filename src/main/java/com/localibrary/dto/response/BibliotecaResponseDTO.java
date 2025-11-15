package com.localibrary.dto.response;

import com.localibrary.entity.Biblioteca;
import com.localibrary.enums.CategoriaBiblioteca;
import com.localibrary.enums.StatusBiblioteca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO básico de resposta para Biblioteca.
 * Versão simplificada sem relacionamentos complexos.
 * Usado em listas e operações básicas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaResponseDTO {

    private Long id;
    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private String telefone;
    private CategoriaBiblioteca categoria;
    private String site;
    private StatusBiblioteca status;
    private String fotoBiblioteca;

    // Endereço (simplificado)
    private String enderecoCompleto;
    private String bairro;
    private String cidade;
    private String estado;
    private BigDecimal latitude;
    private BigDecimal longitude;

    /**
     * Construtor a partir da entidade Biblioteca
     */
    public BibliotecaResponseDTO(Biblioteca biblioteca) {
        this.id = biblioteca.getId();
        this.nomeFantasia = biblioteca.getNomeFantasia();
        this.razaoSocial = biblioteca.getRazaoSocial();
        this.cnpj = biblioteca.getCnpj();
        this.telefone = biblioteca.getTelefone();
        this.categoria = biblioteca.getCategoria();
        this.site = biblioteca.getSite();
        this.status = biblioteca.getStatus();
        this.fotoBiblioteca = biblioteca.getFotoBiblioteca();

        // Endereço
        if (biblioteca.getEndereco() != null) {
            this.enderecoCompleto = String.format("%s, %s - %s, %s - %s, %s",
                    biblioteca.getEndereco().getLogradouro(),
                    biblioteca.getEndereco().getNumero(),
                    biblioteca.getEndereco().getBairro(),
                    biblioteca.getEndereco().getCidade(),
                    biblioteca.getEndereco().getEstado(),
                    biblioteca.getEndereco().getCep()
            );
            this.bairro = biblioteca.getEndereco().getBairro();
            this.cidade = biblioteca.getEndereco().getCidade();
            this.estado = biblioteca.getEndereco().getEstado();
            this.latitude = biblioteca.getEndereco().getLatitude();
            this.longitude = biblioteca.getEndereco().getLongitude();
        }
    }
}