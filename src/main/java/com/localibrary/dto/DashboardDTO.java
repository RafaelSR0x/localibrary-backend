package com.localibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDTO {
    private long totalBibliotecas;
    private long bibliotecasAtivas;
    private long bibliotecasPendentes;
    private long totalLivrosCadastrados; // Total de LivroBase
    private long totalExemplares; // Soma das quantidades em acervos
}