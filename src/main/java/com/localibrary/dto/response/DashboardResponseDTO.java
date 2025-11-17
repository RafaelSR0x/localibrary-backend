package com.localibrary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de resposta do dashboard administrativo.
 *
 * RF-16: Dashboard com estatísticas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    // Estatísticas gerais
    private Long totalBibliotecas;
    private Long bibliotecasAtivas;
    private Long bibliotecasPendentes;
    private Long bibliotecasInativas;

    private Long totalLivros;
    private Long totalGeneros;
    private Long totalExemplares; // Soma de todas as quantidades

    // Bibliotecas recentes (últimas 5 cadastradas)
    private List<BibliotecaResponseDTO> bibliotecasRecentes;

    // Livros mais populares (top 10)
    private List<LivroPopularDTO> livrosPopulares;

    // Bibliotecas pendentes de aprovação
    private List<BibliotecaResponseDTO> bibliotecasPendentesLista;

    /**
     * DTO interno para livros populares com contagem
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LivroPopularDTO {
        private LivroResponseDTO livro;
        private Long quantidadeBibliotecas; // Em quantas bibliotecas está
        private Long quantidadeTotal; // Total de exemplares
    }
}