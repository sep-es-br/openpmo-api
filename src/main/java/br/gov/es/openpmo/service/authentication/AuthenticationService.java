package br.gov.es.openpmo.service.authentication;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.dto.person.queries.PersonAndEmailQuery;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.AuthService;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.service.actors.IsAuthenticatedByService;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AuthenticationService {

    private static final String EMAIL = "email";
    private static final String AUTHORIZATION = "Authorization";

    private static final Logger log = Logger.getLogger(AuthenticationService.class.getName());
    private final TokenService tokenService;
    private final PersonService personService;
    private final IsAuthenticatedByService isAuthenticatedByService;
    @Value("${users.administrators}")
    private List<String> administrators;
    @Value("${spring.security.oauth2.client.provider.idsvr.issuer-uri}")
    private String issuerUri;

    @Autowired
    public AuthenticationService(
            final TokenService tokenService,
            final PersonService personService,
            final IsAuthenticatedByService isAuthenticatedByService
    ) {
        this.tokenService = tokenService;
        this.personService = personService;
        this.isAuthenticatedByService = isAuthenticatedByService;
    }

    public AcessoDto authenticate(final String token) throws Exception {
        final JSONObject personInfo = this.getUserInfo(token);
        final String email = personInfo.getString(EMAIL);

        final Optional<Person> person = this.personService.findByEmail(email);

        if (person.isPresent()) {
            final Person user = person.get();
            final String authenticationToken = this.tokenService.generateToken(user, email, TokenType.AUTHENTICATION);
            final String refreshToken = this.tokenService.generateToken(user, email, TokenType.REFRESH);
            return new AcessoDto(authenticationToken, refreshToken);
        }
        if (this.administrators.contains(email)) {
            final Person user = this.createPerson(personInfo, email);
            final String authenticationToken = this.tokenService.generateToken(user, email, TokenType.AUTHENTICATION);
            final String refreshToken = this.tokenService.generateToken(user, email, TokenType.REFRESH);
            return new AcessoDto(authenticationToken, refreshToken);
        }
        return null;
    }

    private JSONObject getUserInfo(final String token) throws Exception {
        final String personInfoUri = this.issuerUri + "/connect/userinfo";
        final HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, "Bearer " + token);

        try (final CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return new JSONObject(EntityUtils.toString(response.getEntity()));
            }
        }

        throw new IllegalArgumentException();
    }

    private Person createPerson(final JSONObject personInfo, final String email) {
        final Person user = new Person();
        String apelido = personInfo.getString("apelido");
        user.setName(apelido);
        user.setFullName(apelido);
        user.setAdministrator(this.administrators.contains(email));

        final AuthService defaultAuthenticationServer = this.isAuthenticatedByService.findDefaultAuthenticationServer();

        final IsAuthenticatedBy isAuthenticatedBy = new IsAuthenticatedBy(
                null,
                this.isAuthenticatedByService.defaultAuthServerName(),
                personInfo.getString(EMAIL),
                user,
                defaultAuthenticationServer
        );

        this.isAuthenticatedByService.save(isAuthenticatedBy);
        return this.personService.save(user);
    }

    public boolean isValidToken(final String token) throws IOException {
        final String personInfoUri = this.issuerUri + "/connect/userinfo";
        final HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, token);

        try (final CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                return false;
            }
        }

        return true;
    }

    public void signOut(final String authorizationHeader, final HttpServletResponse response) throws IOException {
        final String personInfoUri = this.issuerUri + "/connect/endsession";
        final HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, authorizationHeader);

        try (final CloseableHttpResponse closeResponse = HttpClients.createDefault().execute(postRequest)) {
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

            final PersonAndEmailQuery user = this.personService.findByIdPersonWithRelationshipAuthServiceAcessoCidadao(
                    Long.parseLong(claims.getSubject()));

            final String authenticationToken = this.tokenService.generateToken(
                    user.getPerson(),
                    user.getEmail(),
                    TokenType.AUTHENTICATION
            );

            return new AcessoDto(authenticationToken, refreshToken);
        }
        throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
    }
}
