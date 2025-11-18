package com.localibrary.dto.response;

import com.localibrary.entity.Biblioteca;
import com.localibrary.entity.Endereco;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BibliotecaResponseDTO {
    private Long id;
    private String nomeFantasia;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public BibliotecaResponseDTO(Biblioteca biblioteca) {
        this.id = biblioteca.getId();
        this.nomeFantasia = biblioteca.getNomeFantasia();

        Endereco e = biblioteca.getEndereco();
        if (e != null) {
            this.latitude = e.getLatitude();
            this.longitude = e.getLongitude();
        }
    }
}