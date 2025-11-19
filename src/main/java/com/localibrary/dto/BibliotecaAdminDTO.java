package com.localibrary.dto;

import com.localibrary.entity.Biblioteca;
import com.localibrary.enums.StatusBiblioteca;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BibliotecaAdminDTO {
    private Long id;
    private String nomeFantasia;
    private String cnpj; // Admin pode ver CNPJ
    private String emailResponsavel; // Útil para contato
    private StatusBiblioteca status;
    private String cidade;
    private LocalDateTime dataCadastro;

    public BibliotecaAdminDTO(Biblioteca b) {
        this.id = b.getId();
        this.nomeFantasia = b.getNomeFantasia();
        this.cnpj = b.getCnpj();
        this.status = b.getStatus();
        this.dataCadastro = b.getCreatedAt();

        if (b.getEndereco() != null) {
            this.cidade = b.getEndereco().getCidade();
        }
        // Assumindo que temos acesso à credencial (se o relacionamento for bidirecional)
        if (b.getCredencial() != null) {
            this.emailResponsavel = b.getCredencial().getEmail();
        }
    }
}