package com.localibrary.dto.response;

import com.localibrary.entity.LivroBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta com detalhes completos de um livro.
 * Usado no endpoint de detalhes (RF-05).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LivroDetalheResponseDTO extends LivroResponseDTO {

    private String resumo;
    private String fotoAutor;
    private List<BibliotecaResponseDTO> bibliotecas;
    private List<LivroResponseDTO> livrosSimilares;

    /**
     * Construtor a partir da entidade LivroBase
     */
    public LivroDetalheResponseDTO(LivroBase livro) {
        super(livro);
        this.resumo = livro.getResumo();
        this.fotoAutor = livro.getFotoAutor();
        this.bibliotecas = new ArrayList<>();
        this.livrosSimilares = new ArrayList<>();
    }

    /**
     * Construtor completo com bibliotecas e livros similares
     */
    public LivroDetalheResponseDTO(
            LivroBase livro,
            List<BibliotecaResponseDTO> bibliotecas,
            List<LivroResponseDTO> livrosSimilares) {
        super(livro);
        this.resumo = livro.getResumo();
        this.fotoAutor = livro.getFotoAutor();
        this.bibliotecas = bibliotecas != null ? bibliotecas : new ArrayList<>();
        this.livrosSimilares = livrosSimilares != null ? livrosSimilares : new ArrayList<>();
    }
}