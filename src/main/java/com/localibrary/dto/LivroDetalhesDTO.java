package com.localibrary.dto;

import com.localibrary.dto.response.LivroResponseDTO;
import com.localibrary.entity.LivroBase;
import lombok.Data;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private Set<String> generos;
    private List<LivroResponseDTO> livrosSimilares;

    // Construtor principal
    public LivroDetalhesDTO(LivroBase livro) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.anoPublicacao = livro.getAnoPublicacao();
        this.editora = livro.getEditora();
        this.capa = livro.getCapa();
        this.resumo = livro.getResumo();
        this.fotoAutor = livro.getFotoAutor();

        // Mapeia os gêneros (como antes)
        this.generos = livro.getGeneros().stream()
                .map(livroGenero -> livroGenero.getGenero().getNomeGenero())
                .collect(Collectors.toSet());
    }

    // Setter para os livros similares (será chamado pelo Service)
    public void setLivrosSimilares(List<LivroBase> similares) {
        this.livrosSimilares = similares.stream()
                .map(LivroResponseDTO::new)
                .collect(Collectors.toList());
    }
}