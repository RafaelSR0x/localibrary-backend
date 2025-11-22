package com.localibrary.controller;

import com.localibrary.dto.BibliotecaRegistrationDTO;
import com.localibrary.dto.request.LoginRequestDTO;
import com.localibrary.dto.response.LoginResponseDTO;
import com.localibrary.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "1. Autenticação", description = "Endpoints públicos para entrada no sistema")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Realizar Login", description = "Autentica Admin, Moderador ou Biblioteca e retorna o Token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cadastrar Biblioteca", description = "Registra uma nova biblioteca. O status inicial será PENDENTE até aprovação do Admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Biblioteca cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou endereço não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email ou CNPJ já existente")
    })
    @PostMapping("/cadastro")
    public ResponseEntity<Void> registerBiblioteca(@Valid @RequestBody BibliotecaRegistrationDTO registrationDTO) {
        authenticationService.registerBiblioteca(registrationDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}