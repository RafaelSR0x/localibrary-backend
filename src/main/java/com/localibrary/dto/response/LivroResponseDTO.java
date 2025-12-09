package com.localibrary.dto.response;

import com.localibrary.entity.Livro;
import lombok.Data;

@Data
public class LivroResponseDTO {
    private Long id;
    private String titulo;
    private String autor;
    private String capa;
    private String resumo;

    public LivroResponseDTO(Livro livro) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.capa = livro.getCapa();
        this.resumo = livro.getResumo();
    }
}