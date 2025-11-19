package com.localibrary.dto;

import com.localibrary.entity.BibliotecaLivro;
import lombok.Data;

@Data
public class LivroAcervoDTO {
    private Long idLivroBase;
    private String titulo;
    private String autor;
    private String isbn;
    private String capa;
    private int quantidade;

    public LivroAcervoDTO(BibliotecaLivro bl) {
        this.idLivroBase = bl.getLivroBase().getId();
        this.titulo = bl.getLivroBase().getTitulo();
        this.autor = bl.getLivroBase().getAutor();
        this.isbn = bl.getLivroBase().getIsbn();
        this.capa = bl.getLivroBase().getCapa();
        this.quantidade = bl.getQuantidade();
    }
}