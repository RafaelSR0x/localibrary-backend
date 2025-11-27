package com.localibrary.controller;

import com.localibrary.dto.ApiErrorDTO;
import com.localibrary.dto.BibliotecaRegistrationDTO;
import com.localibrary.dto.request.LoginRequestDTO;
import com.localibrary.dto.response.LoginResponseDTO;
import com.localibrary.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    /**
     * ✅ DOCUMENTAÇÃO COMPLETA: RF-09 e RF-15 - Login
     */
    @Operation(
            summary = "Realizar Login",
            description = "Autentica Admin, Moderador ou Biblioteca e retorna o Token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de sucesso",
                                    value = """
                        {
                          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "tokenType": "Bearer",
                          "expiresIn": 86400
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou faltando campos obrigatórios",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de erro de validação",
                                    value = """
                        {
                          "status": 400,
                          "message": "Dados inválidos",
                          "timestamp": "2025-11-26T10:30:00",
                          "errors": [
                            "email: Email é obrigatório",
                            "senha: Senha é obrigatória"
                          ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas (email ou senha incorretos)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de credenciais inválidas",
                                    value = """
                        {
                          "status": 401,
                          "message": "Acesso não autorizado",
                          "timestamp": "2025-11-26T10:30:00",
                          "errors": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ DOCUMENTAÇÃO COMPLETA: RF-08 - Cadastro de Biblioteca
     */
    @Operation(
            summary = "Cadastrar Biblioteca",
            description = "Registra uma nova biblioteca. O status inicial será PENDENTE até aprovação do Admin. " +
                    "O endereço é validado via API de geolocalização."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Biblioteca cadastrada com sucesso. Aguardando aprovação do administrador."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos, endereço não encontrado ou coordenadas inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de validação",
                                    value = """
                        {
                          "status": 400,
                          "message": "Dados inválidos",
                          "timestamp": "2025-11-26T10:30:00",
                          "errors": [
                            "cnpj: CNPJ inválido (formato: 00.000.000/0000-00)",
                            "cep: CEP inválido (formato: 00000-000)",
                            "email: Email inválido"
                          ]
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email ou CNPJ já existente no sistema",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de conflito",
                                    value = """
                        {
                          "status": 409,
                          "message": "Este email já está em uso: biblioteca@email.com",
                          "timestamp": "2025-11-26T10:30:00",
                          "errors": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Serviço de geolocalização indisponível",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de serviço indisponível",
                                    value = """
                        {
                          "status": 503,
                          "message": "Serviço de geolocalização temporariamente indisponível. Tente novamente.",
                          "timestamp": "2025-11-26T10:30:00",
                          "errors": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class)
                    )
            )
    })
    @PostMapping("/cadastro")
    public ResponseEntity<Void> registerBiblioteca(@Valid @RequestBody BibliotecaRegistrationDTO registrationDTO) {
        authenticationService.registerBiblioteca(registrationDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}