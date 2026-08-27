/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.dto.AcessoDto;
import br.gov.es.openpmo.service.authentication.AuthenticationService;
import br.gov.es.openpmo.service.authentication.CookieService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private static final String FRONT_CALLBACK_URL = "front_callback_url";
    
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final AuthenticationService authenticationService;
    
    private final CookieService cookieService;
    
    public OAuth2LoginSuccessHandler(
            final OAuth2AuthorizedClientService auth2AuthorizedClientService,
            final AuthenticationService authenticationService,
            final CookieService cookieService
    ) {
        this.authorizedClientService = auth2AuthorizedClientService;
        this.authenticationService = authenticationService;
        this.cookieService = cookieService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client = this.authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), 
                oauthToken.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();
        
        final AcessoDto acessoDto = this.authenticationService.authenticate(accessToken, oauthToken.getAuthorizedClientRegistrationId());
        final String valor = acessoDto == null
          ? ApplicationMessage.ERROR_UNAUTHORIZED
          : Base64.getEncoder().withoutPadding().encodeToString(
          new ObjectMapper().writeValueAsString(acessoDto).getBytes());
        response.sendRedirect(this.buildFrontCallbackUrl(request, response, valor));
        
        
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

    
    
    
}
