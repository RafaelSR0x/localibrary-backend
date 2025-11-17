package com.localibrary.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class HealthCheckController {
    /**
     * Endpoint de teste (opcional).
     * Verifica se a API está rodando.
     * * @return Mensagem de boas-vindas
     */
    @GetMapping
    public String healthCheck() {
        // Este endpoint é público e serve como
        // RF-01 (Exibir a página inicial padrão) por enquanto.
        return "API Localibrary MVP v1.0 - Ambiente OK!";
    }
}