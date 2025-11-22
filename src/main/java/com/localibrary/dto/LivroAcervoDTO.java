package com.localibrary.dto;

import com.localibrary.entity.BibliotecaLivro;
import lombok.Data;

@Data
public class LivroAcervoDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private String capa;
    private Integer quantidade;

    public LivroAcervoDTO(BibliotecaLivro bl) {
        this.id = bl.getLivro().getId();
        this.titulo = bl.getLivro().getTitulo();
        this.autor = bl.getLivro().getAutor();
        this.isbn = bl.getLivro().getIsbn();
        this.capa = bl.getLivro().getCapa();
        this.quantidade = bl.getQuantidade();
    }
}