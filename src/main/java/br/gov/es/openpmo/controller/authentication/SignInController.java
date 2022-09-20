package br.gov.es.openpmo.controller.authentication;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.service.authentication.AuthenticationService;
import br.gov.es.openpmo.service.authentication.CookieService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Api(hidden = true)
@RestController
@RequestMapping("/signin")
public class SignInController {

  private static final String FRONT_CALLBACK_URL = "front_callback_url";
  private final CookieService cookieService;
  private final AuthenticationService authenticationService;

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
    final HttpServletRequest request,
    final HttpServletResponse response
  ) throws Exception {
    final AcessoDto acessoDto = this.authenticationService.authenticate(token);
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
    this.cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/openpmo");
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

}
