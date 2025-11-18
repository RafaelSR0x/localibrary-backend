package com.localibrary.dto;

import com.localibrary.entity.Biblioteca;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BibliotecaParaLivroDTO {
    private Long id;
    private String nomeFantasia;
    private String telefone;
    private EnderecoDTO endereco;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public BibliotecaParaLivroDTO(Biblioteca b) {
        this.id = b.getId();
        this.nomeFantasia = b.getNomeFantasia();
        this.telefone = b.getTelefone();
        if (b.getEndereco() != null) {
            this.endereco = new EnderecoDTO(b.getEndereco());
            this.latitude = b.getEndereco().getLatitude();
            this.longitude = b.getEndereco().getLongitude();
        }
    }
}