package com.localibrary.controller;

import com.localibrary.dto.BibliotecaDetalhesDTO;
import com.localibrary.dto.response.BibliotecaResponseDTO;
import com.localibrary.service.BibliotecaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bibliotecas")
public class BibliotecaController {

    @Autowired
    private BibliotecaService bibliotecaService;

    // RF-04: Exibir um mapa com todas as bibliotecas ATIVAS
    @GetMapping
    public ResponseEntity<List<BibliotecaResponseDTO>> listarBibliotecas() {
        return ResponseEntity.ok(bibliotecaService.listarBibliotecasAtivas());
    }

    // RF-07: Exibir detalhes completos de uma biblioteca
    @GetMapping("/{id_biblioteca}")
    public ResponseEntity<BibliotecaDetalhesDTO> verDetalhesBiblioteca(@PathVariable Long id_biblioteca) {
        return ResponseEntity.ok(bibliotecaService.buscarDetalhesBiblioteca(id_biblioteca));
    }
}