package br.gov.es.openpmo.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;

@Service
public class AuthenticationService {

    private final TokenService tokenService;
    private final PersonService personService;
    private static final String EMAIL = "email";
    private static final String AUTHORIZATION = "Authorization";

    @Value("${users.administrators}")
    private List<String> administrators;

    static Logger log = Logger.getLogger(AuthenticationService.class.getName());

    @Autowired
    public AuthenticationService(TokenService tokenService, PersonService personService) {
        this.tokenService = tokenService;
        this.personService = personService;
    }

    @Value("${spring.security.oauth2.client.provider.idsvr.issuer-uri}")
    private String issuerUri;

    public AcessoDto authenticate(String token) throws Exception {
        JSONObject personInfo = getUserInfo(token);
        String email = personInfo.getString(EMAIL);
        Optional<Person> person = personService.findByEmail(email);
        Person usuario = null;
        if (person.isPresent()) {
            usuario = person.get();
            String authenticationToken = tokenService.generateToken(usuario, TokenType.AUTHENTICATION);
            String refreshToken = tokenService.generateToken(usuario, TokenType.REFRESH);
            return new AcessoDto(authenticationToken, refreshToken);
        }
        if (administrators.contains(email)) {
            usuario = createPerson(personInfo);
            String authenticationToken = tokenService.generateToken(usuario, TokenType.AUTHENTICATION);
            String refreshToken = tokenService.generateToken(usuario, TokenType.REFRESH);
            return new AcessoDto(authenticationToken, refreshToken);
        }
        return null;
    }

    public boolean isValidToken(String token) throws IOException {
        String personInfoUri = issuerUri + "/connect/userinfo";
        HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, token);

        try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                return false;
            }
        }

        return true;
    }

    public void signOut(String authorizationHeader, HttpServletResponse response) throws IOException {
        String personInfoUri = issuerUri + "/connect/endsession";
        HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, authorizationHeader);

        try (CloseableHttpResponse closeResponse = HttpClients.createDefault().execute(postRequest)) {
            int status = closeResponse.getStatusLine().getStatusCode();
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

    private JSONObject getUserInfo(String token) throws Exception {
        String personInfoUri = issuerUri + "/connect/userinfo";
        HttpPost postRequest = new HttpPost(personInfoUri);
        postRequest.addHeader(AUTHORIZATION, "Bearer " + token);

        try (CloseableHttpResponse response = HttpClients.createDefault().execute(postRequest)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return new JSONObject(EntityUtils.toString(response.getEntity()));
            }
        }

        throw new IllegalArgumentException();
    }

    private Person findOrCreatePerson(JSONObject personInfo) {
        Optional<Person> person = personService.findByEmail(personInfo.getString(EMAIL));

        if (person.isPresent()) {
            Person usuario = person.get();
            usuario = personService.save(usuario);
            return usuario;
        }

        return createPerson(personInfo);
    }

    private Person createPerson(JSONObject personInfo) {
        Person usuario = new Person();
        usuario.setEmail(personInfo.getString(EMAIL));
        usuario.setName(personInfo.getString("apelido"));
        usuario.setContactEmail(personInfo.getString(EMAIL));
        usuario.setAdministrator(administrators.contains(usuario.getEmail()));
        return personService.save(usuario);
    }

    public AcessoDto refresh(String refreshToken) {
        String authenticationToken;
        Claims claims;
        if (tokenService.isValidToken(refreshToken, TokenType.REFRESH)) {
            claims = tokenService.getUser(refreshToken, TokenType.REFRESH);
            Person usuario = personService.findById(Long.parseLong(claims.getSubject()));
            authenticationToken = tokenService.generateToken(usuario, TokenType.AUTHENTICATION);
            return new AcessoDto(authenticationToken, refreshToken);
        }
        throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
    }
}
