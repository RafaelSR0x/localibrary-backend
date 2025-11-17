package com.localibrary.dto.response;

import com.localibrary.entity.LivroBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO básico de resposta para Livro.
 * Usado em listagens e buscas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivroResponseDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editora;
    private Integer anoPublicacao;
    private String capa;
    private List<String> generos;

    /**
     * Construtor a partir da entidade LivroBase
     */
    public LivroResponseDTO(LivroBase livro) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.editora = livro.getEditora();
        this.anoPublicacao = livro.getAnoPublicacao();
        this.capa = livro.getCapa();

        // Extrair nomes dos gêneros
        if (livro.getGeneros() != null && !livro.getGeneros().isEmpty()) {
            this.generos = livro.getGeneros().stream()
                    .map(lg -> lg.getGenero().getNomeGenero())
                    .collect(Collectors.toList());
        }
    }
}