package com.localibrary.enums;

import lombok.Getter;

/**
 * Enum que representa as categorias de bibliotecas.
 * Baseado na estrutura do banco de dados.
 */
@Getter
public enum CategoriaBiblioteca {
    PUBLICA("Pública"),
    PRIVADA("Privada"),
    UNIVERSITARIA("Universitária"),
    ESCOLAR("Escolar");

    private final String descricao;

    CategoriaBiblioteca(String descricao) {
        this.descricao = descricao;
    }

}
