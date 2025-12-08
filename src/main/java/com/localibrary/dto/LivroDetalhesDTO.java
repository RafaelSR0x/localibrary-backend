package com.localibrary.dto;

import com.localibrary.dto.response.LivroResponseDTO;
import com.localibrary.entity.BibliotecaLivro;
import com.localibrary.entity.Livro;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ? OTIMIZADO: DTO �nico para detalhes de livro
 * Serve para:
 * - GET /livros/{id} (p�blico - com livros similares)
 * - GET /bibliotecas/{id}/livros/{id} (edi��o - com quantidade e IDs de g�neros)
 */
@Data
public class LivroDetalhesDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer anoPublicacao;
    private String editora;
    private String capa;
    private String resumo;
    private String fotoAutor;

    // Para exibi��o (p�blico)
    private Set<String> generos;

    // Para formul�rio de edi��o (privado)
    private Set<Long> generosIds;

    // Para acervo (privado)
    private Integer quantidade;

    // Para recomenda��es (p�blico)
    private List<LivroResponseDTO> livrosSimilares;

    /**
     * Construtor para visualiza��o P�BLICA (RF-05)
     * Usado em: GET /livros/{id}
     */
    public LivroDetalhesDTO(Livro livro) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.anoPublicacao = livro.getAnoPublicacao();
        this.editora = livro.getEditora();
        this.capa = livro.getCapa();
        this.resumo = livro.getResumo();
        this.fotoAutor = livro.getFotoAutor();

        // Nomes dos g�neros (para exibi��o)
        this.generos = livro.getGeneros().stream()
                .map(livroGenero -> livroGenero.getGenero().getNomeGenero())
                .collect(Collectors.toSet());
    }

    /**
     * ? NOVO: Construtor para EDI��O (RF-13)
     * Usado em: GET /bibliotecas/{id}/livros/{id}
     * Inclui IDs de g�neros e quantidade
     */
    public LivroDetalhesDTO(BibliotecaLivro bl) {
        Livro livro = bl.getLivro();

        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.anoPublicacao = livro.getAnoPublicacao();
        this.editora = livro.getEditora();
        this.capa = livro.getCapa();
        this.resumo = livro.getResumo();
        this.fotoAutor = livro.getFotoAutor();

        // ? IDs dos g�neros (para selects/checkboxes no formul�rio)
        this.generosIds = livro.getGeneros().stream()
                .map(lg -> lg.getGenero().getId())
                .collect(Collectors.toSet());

        // ? Quantidade no acervo
        this.quantidade = bl.getQuantidade();
    }

    /**
     * Setter para adicionar livros similares (RF-05)
     * Usado apenas na visualiza��o p�blica
     */
    public void setLivrosSimilares(List<Livro> similares) {
        this.livrosSimilares = similares.stream()
                .map(LivroResponseDTO::new)
                .collect(Collectors.toList());
    }
}
