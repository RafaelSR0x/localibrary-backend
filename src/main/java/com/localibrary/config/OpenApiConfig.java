package com.localibrary.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * ✅ COMPLETO: Configuração do OpenAPI/Swagger com respostas de erro globais
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Localibrary API")
                        .version("1.0.0")
                        .description("""
                                API RESTful para localização e gestão de bibliotecas em São Paulo.
                                
                                ## Funcionalidades Principais
                                - 📚 Busca de livros por título
                                - 🗺️ Localização de bibliotecas próximas
                                - 🔐 Autenticação JWT
                                - 📖 Gestão de acervo
                                - 👥 Painel administrativo
                                
                                ## Autenticação
                                Para acessar rotas protegidas:
                                1. Faça login em `/auth/login`
                                2. Copie o `token` da resposta
                                3. Clique em "Authorize" (🔒) e cole: `Bearer {seu_token}`
                                
                                ## Códigos de Status HTTP
                                - **200** - Sucesso
                                - **201** - Criado com sucesso
                                - **204** - Sem conteúdo (operação bem-sucedida)
                                - **400** - Dados inválidos
                                - **401** - Não autenticado
                                - **403** - Sem permissão
                                - **404** - Recurso não encontrado
                                - **409** - Conflito (recurso já existe)
                                - **500** - Erro interno do servidor
                                - **503** - Serviço indisponível
                                """)
                        .contact(new Contact()
                                .name("Time de Desenvolvimento")
                                .email("dev@localibrary.com")
                                .url("https://github.com/osantosrei/localibrary-api"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))

                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Ambiente de Desenvolvimento"),
                        new Server()
                                .url("https://api.localibrary.com")
                                .description("Ambiente de Produção")
                ))

                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint /auth/login"))

                        // ✅ NOVO: Respostas de erro globais
                        .addResponses("BadRequest", new ApiResponse()
                                .description("Dados inválidos ou requisição malformada")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("Unauthorized", new ApiResponse()
                                .description("Token JWT ausente, inválido ou expirado")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("Forbidden", new ApiResponse()
                                .description("Acesso negado. Você não tem permissão para este recurso.")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("NotFound", new ApiResponse()
                                .description("Recurso não encontrado")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("Conflict", new ApiResponse()
                                .description("Conflito. Recurso já existe (email, CNPJ ou ISBN duplicado)")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("InternalServerError", new ApiResponse()
                                .description("Erro interno do servidor")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))

                        .addResponses("ServiceUnavailable", new ApiResponse()
                                .description("Serviço temporariamente indisponível (ex: API de geolocalização)")
                                .content(new Content()
                                        .addMediaType("application/json",
                                                new MediaType().schema(new Schema().$ref("#/components/schemas/ApiErrorDTO")))))
                );
    }
}