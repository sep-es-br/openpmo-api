package br.gov.es.openpmo.apis.organograma;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OrganogramaApiImpl implements OrganogramaApi {

    static final Map<String, Optional<String>> cacheUnidadeSigla = new HashMap<>();
    static String accessToken = null;

    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private static final int HTTP_OK = 200;

    private final Logger logger;

    @Value("${api.e-docs.grant_type}")
    private String grantType;

    @Value("${api.e-docs.scope}")
    private String scopes;

    @Value("${api.e-docs.client-id}")
    private String clientId;

    @Value("${api.e-docs.client-secret}")
    private String clientSecret;

    @Value("${api.e-docs.uri.token}")
    private String edocsUriToken;

    @Value("${api.organograma.uri.webapi}")
    private String organogramaApi;

    @Autowired
    public OrganogramaApiImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Optional<String> findSiglaByUnidade(String idUnidade) {
        boolean containsKey = cacheUnidadeSigla.containsKey(idUnidade);
        if (containsKey) {
            return cacheUnidadeSigla.get(idUnidade);
        }
        try {
            Optional<String> stringOptional = this.getOrgaoByUnidade(idUnidade)
                    .map(orgao -> orgao.optJSONObject("organizacao"))
                    .map(organizacao -> organizacao.optString("sigla"));
            cacheUnidadeSigla.put(idUnidade, stringOptional);
            return stringOptional;
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }

    private Optional<JSONObject> getOrgaoByUnidade(String idUnidade) {
        final String uri = this.organogramaApi.concat("/unidades/").concat(idUnidade);
        this.logger.info("Executing GET in {}", uri);
        final HttpUriRequest getRequest = new HttpGet(uri);
        getRequest.addHeader(AUTHORIZATION, BEARER + this.fetchClientToken());
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(getRequest)) {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != HTTP_OK) {
                statusLine.getReasonPhrase();
                throw new IllegalStateException(ApplicationMessage.FAILED_FETCH_STATUS_NOT_OK);
            }
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            return Optional.of(json);
        } catch (final IOException e) {
            throw new IllegalStateException(ApplicationMessage.FAILED_FETCH_EXTERNAL_DATA);
        }
    }

    private String fetchClientToken() {
        if (accessToken != null) {
            return accessToken;
        }

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("grant_type", this.grantType));
        parameters.add(new BasicNameValuePair("scope", this.scopes));
        HttpPost postRequest = new HttpPost(this.edocsUriToken);
        postRequest.addHeader(AUTHORIZATION, getAuthorizationValue());
        postRequest.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() != HTTP_OK) {
                throw new IllegalStateException(ApplicationMessage.FAILED_FETCH_STATUS_NOT_OK);
            }
            final JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
            accessToken = result.getString("access_token");
            this.logger.info("Token received successfully");
            return accessToken;
        } catch (final IOException e) {
            throw new NegocioException(ApplicationMessage.FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
        }
    }

    private String getAuthorizationValue() {
        String basicToken = MessageFormat.format("{0}:{1}", this.clientId, this.clientSecret);
        return MessageFormat.format("Basic {0}", Base64.getEncoder().encodeToString(basicToken.getBytes()));
    }

}
