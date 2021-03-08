package br.gov.es.openpmo.controller;

import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.service.AuthenticationService;
import br.gov.es.openpmo.service.CookieService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.swagger.annotations.Api;

@Api(hidden = true)
@RestController
@RequestMapping(value = "/signin")
public class SigninController {

    private static final String FRONT_CALLBACK_URL = "front_callback_url";
    @Autowired
    private CookieService cookieService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/acesso-cidadao-response")
    public RedirectView acessoCidadao(@RequestParam(name = "access_token") String token, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        AcessoDto acessoDto = authenticationService.authenticate(token);
        String valor = acessoDto == null
                       ? ApplicationMessage.ERROR_UNAUTHORIZED
                       : Base64.getEncoder().withoutPadding().encodeToString(
                           new ObjectMapper().writeValueAsString(acessoDto).getBytes());
        return new RedirectView(buildFronstCallBackUrl(request, response, valor));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AcessoDto> refresh(@RequestParam(name = "refreshToken") String refreshToken) {
        AcessoDto signinDto = authenticationService.refresh(refreshToken);

        return ResponseEntity.ok().body(signinDto);
    }

    private String buildFronstCallBackUrl(HttpServletRequest request, HttpServletResponse response, String valor) {
        Cookie cookie = cookieService.findCookie(request, FRONT_CALLBACK_URL);
        cookieService.deleteCookie(request, response, FRONT_CALLBACK_URL, "/openpmo");
        String url = cookie.getValue();
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf('?'));
        }
        return url.concat("/#/home?AcessoDto=".concat(valor));
    }
}
