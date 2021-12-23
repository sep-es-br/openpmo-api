package br.gov.es.openpmo.apis.acessocidadao;

import br.gov.es.openpmo.apis.acessocidadao.response.OperationalOrganizationResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentEmailResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentResponse;
import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentRoleResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.service.journals.JournalCreator;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static br.gov.es.openpmo.utils.ApplicationMessage.FAILED_FETCH_TOKEN_ACESSO_CIDADAO;
import static br.gov.es.openpmo.utils.ApplicationMessage.ORGANIZATION_NOT_FOUND;

@Component
@Scope("singleton")
public class AcessoCidadaoApiImpl implements AcessoCidadaoApi {

  private static final String AUTHORIZATION = "Authorization";

  private static final String BEARER = "Bearer ";

  private static final int HTTP_OK = 200;

  private final Logger logger;

  private final JournalCreator journalCreator;

  @Value("${api.acessocidadao.grant_type}")
  private String grantType;

  @Value("${api.acessocidadao.scope}")
  private String scopes;

  @Value("${api.acessocidadao.client-id}")
  private String clientId;

  @Value("${api.acessocidadao.client-secret}")
  private String clientSecret;

  @Value("${api.acessocidadao.uri.webapi}")
  private String acessocidadaoUriWebApi;

  @Value("${api.acessocidadao.uri.token}")
  private String acessocidadaoUriToken;

  private List<PublicAgentResponse> allPublicAgentResponses;

  public AcessoCidadaoApiImpl(final Logger logger, final JournalCreator journalCreator) {
    this.logger = logger;
    this.journalCreator = journalCreator;
  }

  private static boolean notEmpty(final String sigla) {
    return !sigla.isEmpty();
  }

  private String getSigla(final OperationalOrganizationResponse org, final String organizationGuid) {
    if(org.getGuid().equalsIgnoreCase(organizationGuid)) {
      return org.getAbbreviation();
    }

    return org.getChildren().stream()
      .map(child -> this.getSigla(child, organizationGuid))
      .filter(AcessoCidadaoApiImpl::notEmpty)
      .findFirst()
      .orElse("");
  }

  @Override
  @Cacheable("operationalOrganizations")
  public List<OperationalOrganizationResponse> findAllOperationalOrganizations(final Long idPerson) {
    return this.getList(
      "/api/organizacoes/organograma-operacional",
      (json, list) -> json.forEach(element -> {
        if(element instanceof JSONObject) {
          final JSONObject obj = (JSONObject) element;
          list.add(new OperationalOrganizationResponse(obj));
        }
      }),
      idPerson
    );
  }

  @Override
  @Cacheable("publicAgentRoles")
  public List<PublicAgentRoleResponse> findRoles(final String guid, final Long idPerson) {
    return this.getList(
      "/api/agentepublico/" + guid + "/papeis",
      (json, list) -> json.forEach(element -> {
        if(element instanceof JSONObject) {
          final JSONObject obj = (JSONObject) element;
          list.add(new PublicAgentRoleResponse(obj));
        }
      }),
      idPerson
    );
  }

  private <T> List<T> getList(final String url, final BiConsumer<JSONArray, List<T>> mapper, final Long idPerson) {
    final List<T> data = new ArrayList<>();
    final String token = this.fetchClientToken(idPerson);
    if(token != null) {
      final String uri = this.acessocidadaoUriWebApi.concat(url);
      this.logger.info("Executing GET in {}", uri);
      final HttpGet get = new HttpGet(uri);
      get.addHeader(AUTHORIZATION, BEARER + token);

      try(final CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
        if(response.getStatusLine().getStatusCode() == HTTP_OK) {
          final JSONArray json = new JSONArray(EntityUtils.toString(response.getEntity()));
          mapper.accept(json, data);
        }
      }
      catch(final IOException e) {
        if(Objects.nonNull(idPerson)) {
          this.journalCreator.failure(idPerson);
        }
        e.printStackTrace();
      }
    }
    return data;
  }

  private String fetchClientToken(final Long idPerson) {
    final String basicToken = this.clientId + ":" + this.clientSecret;

    this.logger.info("Executing POST in {}", this.acessocidadaoUriToken);
    final HttpPost postRequest = new HttpPost(this.acessocidadaoUriToken);

    final List<NameValuePair> urlParameters = new ArrayList<>();

    urlParameters.add(new BasicNameValuePair("grant_type", this.grantType));
    urlParameters.add(new BasicNameValuePair("scope", this.scopes));

    postRequest.addHeader(
      AUTHORIZATION,
      "Basic " + Base64.getEncoder().encodeToString(basicToken.getBytes())
    );

    postRequest.setEntity(new UrlEncodedFormEntity(urlParameters, Consts.UTF_8));

    try(final CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
      if(response.getStatusLine().getStatusCode() == HTTP_OK) {
        final JSONObject result = new JSONObject(EntityUtils.toString(response.getEntity()));
        this.logger.info("Token received successfully");
        return result.getString("access_token");
      }
    }
    catch(final IOException e) {
      if(Objects.nonNull(idPerson)) {
        this.journalCreator.failure(idPerson);
      }
      e.printStackTrace();
      throw new NegocioException(FAILED_FETCH_TOKEN_ACESSO_CIDADAO);
    }
    return null;
  }

  @Override
  @Cacheable("publicAgents")
  public List<PublicAgentResponse> findAllPublicAgents(final Long idPerson) {
    if(this.isPublicAgentsAlreadyInMemory()) {
      return Collections.unmodifiableList(this.allPublicAgentResponses);
    }
    final OperationalOrganizationResponse operationalOrganizationResponse = this.findOperationalOrganization("GOVES", idPerson);
    return this.getList(
      "/api/conjunto/" + operationalOrganizationResponse.getGuid() + "/agentesPublicos",
      (json, list) -> json.forEach(element -> {
        if(element instanceof JSONObject) {
          final JSONObject obj = (JSONObject) element;
          list.add(new PublicAgentResponse(obj));
        }
      }),
      idPerson
    );
  }

  @Override
  @Cacheable("agentEmail")
  public Optional<PublicAgentEmailResponse> findAgentEmail(final String sub, final Long idPerson) {
    return Optional.ofNullable(
      this.getEntity(
        "/api/cidadao/" + sub + "/email",
        json -> {
          final String email = json.optString("email");
          if(email == null) {
            return null;
          }
          return new PublicAgentEmailResponse(json);
        },
        idPerson
      )
    );
  }

  private <T> T getEntity(final String url, final Function<? super JSONObject, T> mapper, final Long idPerson) {
    final String token = this.fetchClientToken(idPerson);
    if(token != null) {
      final String uri = this.acessocidadaoUriWebApi.concat(url);
      this.logger.info("Executing GET in {}", uri);
      final HttpGet get = new HttpGet(uri);
      get.addHeader(AUTHORIZATION, BEARER + token);
      try(final CloseableHttpResponse response = HttpClients.createDefault().execute(get)) {
        if(response.getStatusLine().getStatusCode() == HTTP_OK) {
          final JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
          return mapper.apply(json);
        }
      }
      catch(final IOException e) {
        if(Objects.nonNull(idPerson)) {
          this.journalCreator.failure(idPerson);
        }
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  @Cacheable("publicAgentSub")
  public Optional<PublicAgentResponse> findPublicAgentBySub(final String sub, final Long idPerson) {
    return Optional.ofNullable(
      this.getEntity(
        "/api/agentepublico/" + sub,
        PublicAgentResponse::new,
        idPerson
      )
    );
  }

  @Override
  public Optional<String> findSubByCpf(final String cpf, final Long idPerson) {
    return Optional.ofNullable(this.getSub(
      "/api/cidadao/" + cpf + "/pesquisaSub",
      idPerson
    ));
  }

  private String getSub(final String url, final Long idPerson) {
    final String token = this.fetchClientToken(idPerson);
    if(token != null) {
      final String uri = this.acessocidadaoUriWebApi.concat(url);
      final HttpPut put = new HttpPut(uri);
      put.addHeader(AUTHORIZATION, BEARER + token);
      try(final CloseableHttpResponse response = HttpClients.createDefault().execute(put)) {
        if(response.getStatusLine().getStatusCode() == HTTP_OK) {
          final JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
          return json.getString("sub");
        }
      }
      catch(final IOException e) {
        if(Objects.nonNull(idPerson)) {
          this.journalCreator.failure(idPerson);
        }
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  @Cacheable("organization")
  public String getWorkLocation(final String organizationGuid, final Long idPerson) {
    return this.findAllOperationalOrganizations(idPerson).stream()
      .map(org -> this.getSigla(org, organizationGuid))
      .filter(AcessoCidadaoApiImpl::notEmpty)
      .findFirst()
      .orElse("");
  }

  @Override
  public void load(final Long idPerson) {
    this.allPublicAgentResponses = this.findAllPublicAgents(idPerson);
  }

  @Override
  public void unload() {
    this.allPublicAgentResponses = null;
  }

  private boolean isPublicAgentsAlreadyInMemory() {
    return this.allPublicAgentResponses != null;
  }

  private OperationalOrganizationResponse findOperationalOrganization(final String guid, final Long idPerson) {
    return this.findAllOperationalOrganizations(idPerson).stream()
      .filter(org -> org.getAbbreviation().equalsIgnoreCase(guid))
      .findFirst()
      .orElseThrow(() -> new NegocioException(ORGANIZATION_NOT_FOUND));
  }

}
