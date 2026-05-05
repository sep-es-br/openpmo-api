package br.gov.es.openpmo.service.authentication;

import br.gov.es.openpmo.apis.acessocidadao.response.PublicAgentEmailResponse;
import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.dto.person.queries.PersonQuery;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.service.actors.CitizenService;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.pmo.identity_parser.pmo_base.service.ProviderService;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private static final String AUTHORIZATION = "Authorization";

  private static final String BEARER = "Bearer ";

  private final TokenService tokenService;

  private final PersonService personService;

  private final IsAuthenticatedByService isAuthenticatedByService;
  private final ProviderService providerService;

  private final CitizenService citizenService;

  @Value("${users.administrators}")
  private List<String> administrators;

  @Value("${spring.security.oauth2.client.provider.idsvr.issuer-uri}")
  private String issuerUri;
  

  @Autowired
  public AuthenticationService(
    final TokenService tokenService,
    final PersonService personService,
    final IsAuthenticatedByService isAuthenticatedByService,
    @Lazy final ProviderService providerService,
    final CitizenService citizenService
  ) {
    this.tokenService = tokenService;
    this.personService = personService;
    this.isAuthenticatedByService = isAuthenticatedByService;
    this.providerService = providerService;
    this.citizenService = citizenService;
  }

  public AcessoDto authenticate(final String token, final String rid) throws IOException {
    
    final Object sub = providerService.getId();
    final String email = providerService.getEmail();
    final String key = Optional.ofNullable(sub).map(String::valueOf).orElse(email);
    
    Optional<Person> person = this.personService.findByKey(key);
    
    if(!person.isPresent())
        person = this.personService.findByEmail(key);


    if (person.isPresent()) {
      final Person user = person.get();
      final PublicAgentEmailResponse userEmails = this.citizenService.findAgentEmail(sub, user.getId());

      if (userEmails != null && userEmails.getCorporateEmail() != null && userEmails.getCorporateEmail().trim().length() > 0) {
        // Pode existir o caso onde o Acesso Cidadão retorna uma string vazia, por isso essa terceira verificação
        email = userEmails.getCorporateEmail();
        IsAuthenticatedBy authRelationship = user.getAuthentications().stream().findFirst().orElse(null);

        if (authRelationship != null) {
          authRelationship.setEmail(email);
          this.personService.save(user);
        }
      }else if (userEmails != null && userEmails.getEmail() != null && userEmails.getEmail().trim().length() > 0) {
        IsAuthenticatedBy authRelationship = user.getAuthentications().stream().findFirst().orElse(null);

        email = userEmails.getEmail();
        if (authRelationship != null) {
          authRelationship.setEmail(email);
          this.personService.save(user);
        }
      }

      final String authenticationToken = this.tokenService.generateToken(user, key, email, TokenType.AUTHENTICATION);
      final String refreshToken = this.tokenService.generateToken(user, key, email, TokenType.REFRESH);
      return new AcessoDto(authenticationToken, refreshToken);
    }
    if (this.administrators.contains(email)) {
      final Person user = this.createPerson(personInfo);
      final String authenticationToken = this.tokenService.generateToken(user, key, email, TokenType.AUTHENTICATION);
      final String refreshToken = this.tokenService.generateToken(user, key, email, TokenType.REFRESH);
      return new AcessoDto(authenticationToken, refreshToken);
    }
    return null;
  }

  private Person createPerson() {
    final Object sub = providerService.getId();
    final String email = providerService.getEmail();
    final String key = Optional.ofNullable(sub).map(String::valueOf).orElse(email);

    final Person user = new Person();
    final String apelido = providerService.getNome();
    user.setName(apelido);
    user.setFullName(apelido);
    user.setAdministrator(this.administrators.contains(email));
    Person personCreated = this.personService.save(user);

    final IsAuthenticatedBy isAuthenticatedBy = new IsAuthenticatedBy();
    isAuthenticatedBy.setPerson(personCreated);
    isAuthenticatedBy.setKey(key);
    isAuthenticatedBy.setEmail(email);
    isAuthenticatedBy.setName(this.isAuthenticatedByService.defaultAuthServerName());
    isAuthenticatedBy.setAuthService(this.isAuthenticatedByService.findDefaultAuthenticationServer());

    IsAuthenticatedBy isAuthenticatedByCreated = this.isAuthenticatedByService.save(isAuthenticatedBy);
    personCreated.getAuthentications().add(isAuthenticatedByCreated);
    return personCreated;
  }

  public void signOut(
      final String authorizationHeader,
      final HttpServletResponse response) throws IOException {
    final String personInfoUri = this.issuerUri + "/connect/endsession";
    final HttpUriRequest postRequest = new HttpPost(personInfoUri);
    postRequest.addHeader(AUTHORIZATION, authorizationHeader);

    try (final CloseableHttpClient httpClient = HttpClients.createDefault();
        final CloseableHttpResponse closeResponse = httpClient.execute(postRequest)) {
      final int status = closeResponse.getStatusLine().getStatusCode();
      if (status == 302 || status == 200) {
        Arrays.asList(closeResponse.getAllHeaders())
            .forEach(header -> response.addHeader(header.getName(), header.getValue()));
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Location", personInfoUri);
        response.setStatus(200);
        response.sendRedirect(personInfoUri);
      } else {
        throw new NegocioException();
      }
    }
  }

  public AcessoDto refresh(final String refreshToken) {
    if (this.tokenService.isValidToken(refreshToken, TokenType.REFRESH)) {
      final Claims claims = this.tokenService.getUser(refreshToken, TokenType.REFRESH);

      final PersonQuery user = this.personService
          .findByIdPersonWithRelationshipAuthServiceAcessoCidadao(Long.parseLong(claims.getSubject()));

      final String authenticationToken =
        this.tokenService.generateToken(user.getPerson(), user.getKey(), user.getEmail(), claims.get("authId", String.class), TokenType.AUTHENTICATION);

      return new AcessoDto(authenticationToken, refreshToken);
    }
    throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
  }
  

}
