package br.gov.es.openpmo.apis.edocs;

import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.service.journals.JournalCreator;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_EXTERNAL_DATA;
import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_STATUS_NOT_OK;
import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_TOKEN_ACESSO_CIDADAO;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

@Component
public class EDocsApiImpl implements EDocsApi {

  private static final String AUTHORIZATION = "Authorization";

  private static final String BEARER = "Bearer ";

  private static final int HTTP_OK = 200;

  private final JournalCreator journalCreator;

  private final Logger logger;

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

  private final OrganogramaApi organogramaApi;
  @Value("${api.e-docs.identificador-externo.processo-prioritario}")
  public String identificadorExterno;
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
  public EDocsApiImpl(
    final JournalCreator journalCreator,
    final Logger logger,
    final OrganogramaApi organogramaApi
  ) {
    this.journalCreator = journalCreator;
    this.logger = logger;
    this.organogramaApi = organogramaApi;
  }

  @Override
  public ProcessResponse findProcessByProtocol(
    final String protocol,
    final Long idPerson
  ) {

    final String token = this.fetchClientToken(idPerson);

    final List<ProcessResponse> processes = this.fetchProcessByProtocol(
      Collections.singletonList(protocol),
      ProcessResponse::new,
      idPerson,
      token
    );

    if (processes.isEmpty()) {
      throw new IllegalStateException(
        "Process not found for protocol: " + protocol
      );
    }
  
    final ProcessResponse processResponse = processes.get(0);

    final boolean processPriority = this.isProcessPriority(processResponse.getId(), idPerson, token);
    processResponse.setPriority(processPriority);

    final List<ProcessHistoryResponse> processHistory = this.findProcessHistoryById(processResponse.getId(), idPerson, token);
    processResponse.addHistory(processHistory);

    return processResponse;
  }

  
  public List<ProcessResponse> findProcessesByProtocolsAsSystem(
    final List<String> protocols
  ) {
    
    final String token = this.fetchSystemToken();

    final List<ProcessResponse> processes = this.fetchProcessByProtocol(
      protocols,
      ProcessResponse::new,
      null,
      token
    );
  
    if (processes.isEmpty()) {
      return Collections.emptyList();
    }
  
    for (ProcessResponse processResponse : processes) {
  
      final boolean processPriority =
        this.isProcessPriority(processResponse.getId(),null, token);
  
      processResponse.setPriority(processPriority);
  
      final List<ProcessHistoryResponse> processHistory =
        this.findProcessHistoryById(processResponse.getId(), null, token);
  
      processResponse.addHistory(processHistory);
    }
  
    return processes;
  }


  @Override
  public List<ProcessHistoryResponse> findProcessHistoryById(
    final String id,
    final Long idPerson,
    final String token
  ) {
    return this.getList(
      "/v2/processos/" + id + "/atos",
      (array, list) -> array.forEach(element -> {
        if(element instanceof JSONObject) {
          final JSONObject obj = (JSONObject) element;
          list.add(new ProcessHistoryResponse(obj, this.organogramaApi));
        }
      }),
      idPerson,
      token
    );
  }

  @Override
  public boolean isProcessPriority(
    final String processId,
    final Long personId,
    final String token
  ) {


    final String uri = this.edocsUriWebApi.concat("/v2/processos/").concat(processId).concat("/sinalizacao");
    this.logger.info("Executing GET in {}", uri);
    final HttpUriRequest getRequest = new HttpGet(uri);
    getRequest.addHeader(AUTHORIZATION, BEARER + token);
    try(final CloseableHttpClient httpClient = HttpClients.createDefault();
        final CloseableHttpResponse response = httpClient.execute(getRequest)) {
      final StatusLine statusLine = response.getStatusLine();
      if(statusLine.getStatusCode() != HTTP_OK) {
        return false;
      }
      final Iterable<Object> array = new JSONArray(EntityUtils.toString(response.getEntity()));
      for(final Object element : array) {
        if(element instanceof JSONObject) {
          final JSONObject obj = (JSONObject) element;
          if(obj.optString("identificadorExterno").equals(this.identificadorExterno)) {
            return true;
          }
        }
      }
    }
    catch(final IOException e) {
      return false;
    }
    return false;
  }

  private List<ProcessResponse> fetchProcessByProtocol(
    List<String> protocols,
    final Function<JSONObject, ProcessResponse> mapper,
    final Long idPerson,
    final String token
  ) {

    int tamanhoPagina = protocols.size() + 1;

    final String uri = this.edocsUriWebApi.concat("/v2/processos/paginated-search");
    this.logger.info("Executing POST in {}", uri);
    final HttpPost postRequest = new HttpPost(uri);

    postRequest.addHeader(AUTHORIZATION, BEARER + token);

    final JSONObject request = this.buildBody(protocols, tamanhoPagina);

    final HttpEntity stringEntity = new StringEntity(request.toString(), APPLICATION_JSON);
    this.logger.info("Body: {}", request);
    postRequest.setEntity(stringEntity);

    try(final CloseableHttpClient httpClient = HttpClients.createDefault();
        final CloseableHttpResponse response = httpClient.execute(postRequest)) {
      if(this.isNotHttp200(response)) {
        response.getStatusLine().getReasonPhrase();
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }
      final JSONArray results =
        new JSONObject(EntityUtils.toString(response.getEntity()))
          .getJSONArray("result");

      final List<ProcessResponse> responses = new ArrayList<>();

      for (int i = 0; i < results.length(); i++) {
        final JSONObject json = results.getJSONObject(i);
        responses.add(mapper.apply(json));
      }

      return responses;
    }
    catch(final IOException e) {
      this.journalCreator.failure(idPerson);
      throw new IllegalStateException(FAILED_FETCH_EXTERNAL_DATA);
    }
  }

  private JSONObject buildBody(
    List<String> protocols,
    int tamanhoPagina
  ) {
    JSONObject request = new JSONObject();
    request.put("protocolos", new JSONArray(protocols));
    request.put("tamanhoPagina", tamanhoPagina);
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

    try(final CloseableHttpClient httpClient = HttpClients.createDefault();
        final CloseableHttpResponse response = httpClient.execute(postRequest)) {
      if(this.isNotHttp200(response)) {
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }

      final JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
      this.logger.info("Token received successfully");
      return result.getString("access_token");
    }
    catch(final IOException e) {
      this.journalCreator.failure(idPerson);
      throw new NegocioException(FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
    }
  }

  private String fetchSystemToken() {
    final String basicToken = this.clientId + ":" + this.clientSecret;

    this.logger.info("Executing POST in {}", this.edocsUriToken);
    final HttpPost postRequest = new HttpPost(this.edocsUriToken);

    final List<NameValuePair> parameters = new ArrayList<>();
    parameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
    parameters.add(new BasicNameValuePair("scope", this.scopes));

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

      this.logger.info("System token received successfully");
      return result.getString("access_token");
    }
    catch (final IOException e) {
      this.logger.error("Error fetching system token", e);
      throw new NegocioException(FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
    }
  }

  private boolean isNotHttp200(final HttpResponse response) {
    return response.getStatusLine().getStatusCode() != HTTP_OK;
  }

  private List<ProcessHistoryResponse> getList(
    final String url,
    final BiConsumer<JSONArray, List<ProcessHistoryResponse>> mapper,
    final Long idPerson,
    final String token
  ) {

    final String uri = this.edocsUriWebApi.concat(url);
    this.logger.info("Executing GET in {}", uri);
    final HttpUriRequest getRequest = new HttpGet(uri);
    getRequest.addHeader(AUTHORIZATION, BEARER + token);

    try(final CloseableHttpClient httpClient = HttpClients.createDefault();
        final CloseableHttpResponse response = httpClient.execute(getRequest)) {

      if(this.isNotHttp200(response)) {
        throw new IllegalStateException(FAILED_FETCH_STATUS_NOT_OK);
      }

      final JSONArray array = new JSONArray(EntityUtils.toString(response.getEntity()));
      final List<ProcessHistoryResponse> history = new ArrayList<>();
      mapper.accept(array, history);
      return history;
    }
    catch(final IOException e) {
      this.journalCreator.failure(idPerson);
    }
    return new ArrayList<>();
  }

}
