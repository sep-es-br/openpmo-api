package br.gov.es.openpmo.apis.edocs;

import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.service.journals.JournalCreator;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Component
public class EDocsApiImpl implements EDocsApi {

  private static final String AUTHORIZATION = "Authorization";

  private static final String BEARER = "Bearer ";

  private static final int HTTP_OK = 200;

  private final JournalCreator journalCreator;

  private final Logger logger;

  @Value("${api.e-docs.grant_type}")
  private String grantType;

  @Value("${api.e-docs.scope}")
  private String scopes;

  @Value("${api.e-docs.client-id}")
  private String clientId;

  @Value("${api.e-docs.client-secret}")
  private String clientSecret;

  @Value("${api.e-docs.uri.webapi}")
  private String edocsUriWebApi;

  @Value("${api.e-docs.uri.token}")
  private String edocsUriToken;

  @Autowired
  public EDocsApiImpl(final JournalCreator journalCreator, final Logger logger) {
    this.journalCreator = journalCreator;
    this.logger = logger;
  }

  @Override
  public ProcessResponse findProcessByProtocol(final String protocol, final Long idPerson) {
    final ProcessResponse processResponse = this.fetchProcessByProtocol(
        protocol,
        ProcessResponse::new,
        idPerson
    );

    final List<ProcessHistoryResponse> processHistory = this.findProcessHistoryById(processResponse.getId(), idPerson);
    processResponse.addHistory(processHistory);

    return processResponse;
  }

  private ProcessResponse fetchProcessByProtocol(
      final String protocol,
      final Function<JSONObject, ProcessResponse> mapper,
      final Long idPerson
  ) {
    final String token = this.fetchClientToken(idPerson);

    final String uri = this.edocsUriWebApi.concat("/v2/processos/search");
    this.logger.info("Executing POST in {}", uri);
    final HttpPost postRequest = new HttpPost(uri);

    postRequest.addHeader(AUTHORIZATION, BEARER + token);

    final JSONObject request = this.buildBody(protocol);

    final HttpEntity stringEntity = new StringEntity(request.toString(), APPLICATION_JSON);
    this.logger.info("Body: {}", request);
    postRequest.setEntity(stringEntity);

    try (final CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
      if (this.isNotHttp200(response)) {
        response.getStatusLine().getReasonPhrase();
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }
      final JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()))
          .getJSONArray("value")
          .getJSONObject(0);
      return mapper.apply(json);
    } catch (final IOException e) {
      this.journalCreator.failure(idPerson);
      throw new IllegalStateException(FAILED_FETCH_EXTERNAL_DATA);
    }
  }

  private JSONObject buildBody(final String protocol) {
    final JSONArray array = new JSONArray();
    array.put(protocol);
    final JSONObject request = new JSONObject();
    request.put("protocolos", array);
    return request;
  }

  private String fetchClientToken(final Long idPerson) {
    final String basicToken = this.clientId + ":" + this.clientSecret;

    this.logger.info("Executing POST in {}", this.edocsUriToken);
    final HttpPost postRequest = new HttpPost(this.edocsUriToken);

    final List<NameValuePair> parameters = new ArrayList<>();

    parameters.add(new BasicNameValuePair("grant_type", this.grantType));
    parameters.add(new BasicNameValuePair("scope", this.scopes));

    postRequest.addHeader(
        AUTHORIZATION,
        "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes())
    );

    postRequest.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));

    try (final CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
      if (this.isNotHttp200(response)) {
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }

      final JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
      this.logger.info("Token received successfully");
      return result.getString("access_token");
    } catch (final IOException e) {
      this.journalCreator.failure(idPerson);
      throw new NegocioException(FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
    }
  }

  private boolean isNotHttp200(final CloseableHttpResponse response) {
    return response.getStatusLine().getStatusCode() != HTTP_OK;
  }

  @Override
  public List<ProcessHistoryResponse> findProcessHistoryById(final String id, final Long idPerson) {
    return this.getList(
        "/v2/processos/" + id + "/atos",
        (array, list) -> array.forEach(element -> {
          if (element instanceof JSONObject) {
            final JSONObject obj = (JSONObject) element;
            list.add(new ProcessHistoryResponse(obj));
          }
        }),
        idPerson
    );
  }

  private List<ProcessHistoryResponse> getList(final String url, final BiConsumer<JSONArray, List<ProcessHistoryResponse>> mapper, final Long idPerson) {
    final String token = this.fetchClientToken(idPerson);

    final String uri = this.edocsUriWebApi.concat(url);
    this.logger.info("Executing GET in {}", uri);
    final HttpUriRequest getRequest = new HttpGet(uri);
    getRequest.addHeader(AUTHORIZATION, BEARER + token);

    try (final CloseableHttpResponse response = HttpClients.createDefault().execute(getRequest)) {

      if (this.isNotHttp200(response)) {
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }

      final JSONArray array = new JSONArray(EntityUtils.toString(response.getEntity()));
      final List<ProcessHistoryResponse> history = new ArrayList<>();
      mapper.accept(array, history);
      return history;
    } catch (final IOException e) {
      this.journalCreator.failure(idPerson);
      throw new IllegalStateException(FAILED_FETCH_EXTERNAL_DATA);
    }
  }

}
