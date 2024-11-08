package br.gov.es.openpmo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

public class RestTemplateUtils {

    /**
     * Método auxiliar que cria uma requisição com Basic Auth
     *
     * @param restTemplate o RestTemplate para gerar a requisição
     * @param url a URL do endpoint ou API descrito
     * @param user o Usuário da credencial
     * @param password a Senha da credencial
     * @return resultSet da requisição
     */
    public ResponseEntity<Object> createRequestWithAuth(RestTemplate restTemplate, String url, String user, String password) {
        String plainCreds = user + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.getEncoder().encode(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
