package com.localibrary.service;

import com.localibrary.dto.response.GoogleGeocodingResponse;
import com.localibrary.exception.ExternalServiceException;
import com.localibrary.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

record Coordinates(BigDecimal latitude, BigDecimal longitude) {
}

@Service
public class GeolocationService {

    private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);

    @Value("${app.google.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * ✅ CORRIGIDO: Agora lança ExternalServiceException se a API falhar
     * Resolve: RNF-15 (fallback) e tratamento de erro 503
     */
    public Optional<Coordinates> getCoordinatesFromAddress(String cep, String logradouro, String numero, String cidade) {
        String address = String.format("%s, %s, %s, %s", logradouro, numero, cidade, cep);

        // Se não houver chave configurada, usa Mock
        if (apiKey == null || apiKey.equals("SUA_CHAVE_DA_GOOGLE_API_AQUI") || apiKey.isEmpty()) {
            logger.warn("Chave da API do Google não configurada. Usando coordenadas mock.");
            return getMockCoordinates(logradouro);
        }

        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                    + address.replace(" ", "+")
                    + "&key=" + apiKey;

            logger.info("Consultando API de geolocalização para: {}", address);

            GoogleGeocodingResponse response = restTemplate.getForObject(url, GoogleGeocodingResponse.class);

            if (response != null && "OK".equals(response.getStatus()) && !response.getResults().isEmpty()) {
                GoogleGeocodingResponse.Location location = response.getResults().get(0).getGeometry().getLocation();

                logger.info("Coordenadas obtidas com sucesso: lat={}, lng={}", location.getLat(), location.getLng());

                return Optional.of(new Coordinates(
                        BigDecimal.valueOf(location.getLat()),
                        BigDecimal.valueOf(location.getLng())
                ));
            } else if (response != null) {
                logger.warn("Google Geocoding API retornou status: {}", response.getStatus());

                // Trata erros específicos da API
                switch (response.getStatus()) {
                    case "ZERO_RESULTS":
                        logger.error("Endereço não encontrado: {}", address);
                        return Optional.empty(); // Retorna vazio, será tratado no service
                    case "OVER_QUERY_LIMIT":
                        throw new ExternalServiceException("Limite de consultas da API de geolocalização excedido. Tente novamente mais tarde.");
                    case "REQUEST_DENIED":
                        throw new ExternalServiceException("Acesso negado à API de geolocalização. Verifique a chave da API.");
                    case "INVALID_REQUEST":
                        logger.error("Requisição inválida para API de geolocalização: {}", address);
                        return Optional.empty();
                    default:
                        throw new ExternalServiceException("Erro ao consultar serviço de geolocalização: " + response.getStatus());
                }
            }

        } catch (ResourceAccessException ex) {
            logger.error("Timeout ou falha de conexão com API de geolocalização", ex);
        } catch (ExternalServiceException ex) {
            throw ex; // Re-lança exceções já tratadas
        } catch (Exception ex) {
            logger.error("Erro inesperado ao consultar API de geolocalização", ex);
        }

        // Fallback para mock em caso de falha
        logger.warn("Usando coordenadas mock como fallback para: {}", address);
        return getMockCoordinates(logradouro);
    }

    /**
     * RNF-15: Fallback com coordenadas mock
     */
    private Optional<Coordinates> getMockCoordinates(String logradouro) {
        if (logradouro != null && logradouro.contains("Paulista")) {
            logger.info("Mock: Usando coordenadas da Avenida Paulista");
            return Optional.of(new Coordinates(new BigDecimal("-23.5614"), new BigDecimal("-46.6560")));
        }

        logger.info("Mock: Usando coordenadas do centro de São Paulo");
        return Optional.of(new Coordinates(
                BigDecimal.valueOf(Constants.DEFAULT_LATITUDE),
                BigDecimal.valueOf(Constants.DEFAULT_LONGITUDE)
        ));
    }
}