package br.gov.es.openpmo.controller.authentication;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.service.authentication.AuthenticationService;
import br.gov.es.openpmo.service.authentication.CookieService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import java.util.Base64;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Api(hidden = true)
@RestController
@RequestMapping("/signin")
public class SignInController {

  private static final String FRONT_CALLBACK_URL = "front_callback_url";
  private final CookieService cookieService;
  private final AuthenticationService authenticationService;
  
  private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> repository =
    new HttpSessionOAuth2AuthorizationRequestRepository();

  public SignInController(
    final CookieService cookieService,
    final AuthenticationService authenticationService
  ) {
    this.cookieService = cookieService;
    this.authenticationService = authenticationService;
  }

  @GetMapping("/acesso-cidadao-response")
  public RedirectView acessoCidadao(
    @RequestParam(name = "access_token") final String token,
    @RequestParam final String state,
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws Exception {
      
       // 1. Decode do state
    Map<String, Object> wrapper = decodeState(state);

    String originalState = (String) wrapper.get("s");
    String ridFromState = (String) wrapper.get("rid");

    // 3. Recuperar request salvo pelo Spring
    OAuth2AuthorizationRequest authRequest =
        repository.loadAuthorizationRequest(request);

    if (authRequest == null) {
        throw new IllegalStateException("AuthorizationRequest não encontrado");
    }

    // 4. Validar state
    if (!state.equals(authRequest.getState())) {
        throw new IllegalStateException("State inválido");
    }

    // (opcional) remover da sessão
    repository.removeAuthorizationRequest(request, response);
          
    final AcessoDto acessoDto = this.authenticationService.authenticate(token, ridFromState);
    final String valor = acessoDto == null
      ? ApplicationMessage.ERROR_UNAUTHORIZED
      : Base64.getEncoder().withoutPadding().encodeToString(
      new ObjectMapper().writeValueAsString(acessoDto).getBytes());
    return new RedirectView(this.buildFrontCallbackUrl(request, response, valor));
  }

  private String buildFrontCallbackUrl(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final String valor
  ) {
    final Cookie cookie = this.cookieService.findCookie(request, FRONT_CALLBACK_URL);
    this.cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/");
    String url = cookie.getValue();
    if(url.contains("?")) {
      url = url.substring(0, url.indexOf('?'));
    }
    return url.concat("/#/home?AcessoDto=".concat(valor));
  }

  @GetMapping("/refresh")
  public ResponseEntity<AcessoDto> refresh(@RequestParam(name = "refreshToken") final String refreshToken) {
    final AcessoDto signinDto = this.authenticationService.refresh(refreshToken);

    return ResponseEntity.ok().body(signinDto);
  }
  
  private Map<String, Object> decodeState(String encodedState) throws Exception {
    byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedState);
    String json = new String(decodedBytes);
    return new JSONObject(json).toMap();
}

}
