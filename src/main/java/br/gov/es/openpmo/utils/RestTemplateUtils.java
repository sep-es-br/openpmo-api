package br.gov.es.openpmo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.util.Base64;

public class RestTemplateUtils {

    private static final Logger logger = LogManager.getLogger(RestTemplateUtils.class);

    /**
     * Cria e configura um {@link RestTemplate} que ignora a verificação de certificados SSL.
     *
     * @return um {@link RestTemplate} configurado para ignorar a verificação de certificados.
     * @throws Exception se ocorrer um erro ao criar ou configurar o contexto SSL.
     */
    public RestTemplate createRestTemplateWithNoSSL() throws Exception {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }

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
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            return ResponseEntity.ok("teste");
        } catch (Exception e) {
            logger.error("Erro ao realizar requisição ao Pentaho: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
