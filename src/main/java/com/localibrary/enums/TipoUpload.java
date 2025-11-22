package com.localibrary.enums;

import lombok.Getter;

@Getter
public enum TipoUpload {
    CAPA("capas", 600, 800),
    AUTOR("autores", 400, 400),
    BIBLIOTECA("bibliotecas", 800, 600);

    private final String diretorio;
    private final int largura;
    private final int altura;

    TipoUpload(String diretorio, int largura, int altura) {
        this.diretorio = diretorio;
        this.largura = largura;
        this.altura = altura;
    }
}