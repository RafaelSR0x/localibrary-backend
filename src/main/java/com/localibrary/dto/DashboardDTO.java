package com.localibrary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDTO {
    private long totalBibliotecas;
    private long bibliotecasAtivas;
    private long bibliotecasPendentes;
    private long totalLivrosCadastrados;
    private long totalExemplares;
}