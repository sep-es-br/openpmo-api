package br.gov.es.openpmo.service.organization;

import br.gov.es.openpmo.exception.NegocioException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.Consts;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_STATUS_NOT_OK;
import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_TOKEN_ACESSO_CIDADAO;


@Service
public class OrganizationTokenService {

    @Value("${spring.security.oauth2.client.registration.org-client.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.org-client.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.provider.org-client.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.registration.org-client.scope}")
    private String scope;
    @Value("${spring.security.oauth2.client.registration.org-client.authorization-grant-type}")
    private String grantType;
    private static final String AUTHORIZATION = "Authorization";
    private static final int HTTP_OK = 200;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public String fetchSystemToken() {
        final String basicToken = this.clientId + ":" + this.clientSecret;

        final HttpPost postRequest = new HttpPost(this.tokenUri);

        scope = scope.replace(",", " ");

        final List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("grant_type", this.grantType));
        parameters.add(new BasicNameValuePair("scope", this.scope));

        postRequest.addHeader(
                AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes(StandardCharsets.UTF_8))
        );

        postRequest.addHeader(CONTENT_TYPE, APPLICATION_X_WWW_FORM_URLENCODED);

        postRequest.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postRequest)) {

            if (this.isNotHttp200(response)) {
                throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
            }

            final JSONObject result =
                    new JSONObject(EntityUtils.toString(response.getEntity()));

            return result.getString("access_token");
        }
        catch (final IOException e) {
            throw new NegocioException(FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
        }
    }

    private boolean isNotHttp200(final HttpResponse response) {
        return response.getStatusLine().getStatusCode() != HTTP_OK;
    }
}
