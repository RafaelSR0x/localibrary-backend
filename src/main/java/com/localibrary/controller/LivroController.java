package com.localibrary.controller;

import com.localibrary.dto.BibliotecaParaLivroDTO;
import com.localibrary.dto.LivroDetalhesDTO;
import com.localibrary.dto.response.LivroResponseDTO;
import com.localibrary.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
@Tag(name = "4. Livros", description = "Busca e descoberta de livros (Público)")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @Operation(summary = "Buscar por Título", description = "Pesquisa livros por parte do título (busca textual).")
    @GetMapping
    public ResponseEntity<List<LivroResponseDTO>> buscarLivros(
            @Parameter(description = "Termo de busca") @RequestParam String titulo
    ) {
        return ResponseEntity.ok(livroService.buscarLivrosPorTitulo(titulo));
    }

    @Operation(summary = "Livros Populares", description = "Retorna os Top 10 livros com maior disponibilidade no sistema.")
    @GetMapping("/populares")
    public ResponseEntity<List<LivroResponseDTO>> buscarPopulares() {
        return ResponseEntity.ok(livroService.buscarLivrosPopulares());
    }

    @Operation(summary = "Detalhes do Livro", description = "Exibe ficha técnica e recomendações de livros similares.")
    @GetMapping("/{id_livro}")
    public ResponseEntity<LivroDetalhesDTO> verDetalhesLivro(@PathVariable Long id_livro) {
        return ResponseEntity.ok(livroService.buscarDetalhesDoLivro(id_livro));
    }

    @Operation(summary = "Onde Encontrar", description = "Lista bibliotecas que possuem o livro. Se informar lat/lon, ordena por proximidade.")
    @GetMapping("/{id_livro}/bibliotecas")
    public ResponseEntity<List<BibliotecaParaLivroDTO>> verBibliotecasDoLivro(
            @PathVariable Long id_livro,
            @Parameter(description = "Latitude do usuário") @RequestParam(required = false) Double lat,
            @Parameter(description = "Longitude do usuário") @RequestParam(required = false) Double lon
    ) {
        return ResponseEntity.ok(livroService.buscarBibliotecasPorLivro(id_livro, lat, lon));
    }
}