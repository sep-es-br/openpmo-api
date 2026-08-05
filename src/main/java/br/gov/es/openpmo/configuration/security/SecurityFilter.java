package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class SecurityFilter extends OncePerRequestFilter {

  private static final Logger log = Logger.getLogger(SecurityFilter.class.getName());

  private final TokenService tokenService;
  
  private final PersonService personService;
  
  private final String authName;

  @Autowired
  public SecurityFilter(
          final TokenService tokenService,
          final PersonService personService,
          @Value("${app.login.server.idsvr.name}") final String authName
  ) {
    this.tokenService = tokenService;
    this.personService = personService;
    this.authName = authName;
  }

  private static boolean isPublicUrl(final String url) {
    return url.endsWith("/refresh")
           || url.endsWith("/signout")
           || (url.contains("/avatar") && url.endsWith("/person"))
           || url.endsWith("/versions")
           || url.endsWith("/configuration")
           || url.endsWith("/acesso-cidadao-response.html")
           || url.endsWith("/acesso-cidadao-response")
           || url.contains("/evidence/image")
           || url.contains("/pentaho");
  }

  private static boolean isSwaggerUrl(final String url) {
    return url.endsWith("/documentation/index.html")
           || url.endsWith("/v2/api-docs")
           || url.contains("/swagger-resources")
           || url.contains("/swagger-ui-standalone-preset.js")
           || url.contains("/swagger-ui-bundle.js")
           || url.contains("/swagger-ui.css")
           || url.contains("/favicon-16x16.ico")
           || url.contains("/logo.png")
           || url.contains("/webjars/");
  }

  private static String getToken(final HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    final String url = request.getRequestURI();
    if(token == null && url.endsWith("/signout")) {
      token = request.getParameter("token");
    }

    if(token == null) {
      throw new AutenticacaoException(ApplicationMessage.TOKEN_NOT_FOUND);
    }

    if(!token.startsWith("Bearer ")) {
      throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
    }

    return token;
  }

  @Override
  public void doFilterInternal(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final FilterChain filterChain
  ) throws IOException, ServletException {
    final String url = request.getRequestURI();

    final boolean isSwaggerUrl = isSwaggerUrl(url);
    final boolean isPublicUrl = isPublicUrl(url);

    if(isPublicUrl || isSwaggerUrl) {
      filterChain.doFilter(request, response);
    }
    else {
      try {
        this.validateToken(request);
                        
                  
        
      }
      catch(final AutenticacaoException a) {
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        final ErroDto erroDto = new ErroDto(a.getMessage());
        final JSONObject erroJON = new JSONObject(erroDto);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(erroJON.toString());
        return;
      }
      catch(final RuntimeException e) {
        log.log(Level.SEVERE, e.toString(), e);
        throw e;
      }
      filterChain.doFilter(request, response);
    }
  }

  private void validateToken(final HttpServletRequest request) {
    final String token = getToken(request).split(" ")[1];
    if(this.tokenService.isValidToken(token, TokenType.AUTHENTICATION)) {
      final String userKey = this.tokenService.getUser(token, TokenType.AUTHENTICATION).get("key", String.class);
      final String userEmail = this.tokenService.getUser(token, TokenType.AUTHENTICATION).get("email", String.class);
      
        Optional<Person> optUserModel = this.personService.findByKey(userKey);
        
        if(!optUserModel.isPresent())
            optUserModel = this.personService.findByEmail(userEmail);
      
        Person userModel = optUserModel.orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
        
        Map<String, Object> attrs = new HashMap<>();
        
        IsAuthenticatedBy authBy = userModel.findAuthenticationDataBy(this.authName).orElseThrow(() -> new IllegalStateException("autenticação não encontrada"));
                
        attrs.put("sub", authBy.getKey());
        attrs.put("nome", userModel.getName());
        attrs.put("email", authBy.getEmail());
        
        List<GrantedAuthority> authority = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
      
        OAuth2User oAuth2User = new DefaultOAuth2User(authority, attrs, "nome");
        
        Claims claims = this.tokenService.getUser(token, TokenType.AUTHENTICATION);
      
      final OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(oAuth2User, Collections.EMPTY_LIST, claims.get("authId", String.class));
      
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      final SecurityContext securityContext = SecurityContextHolder.getContext();
      securityContext.setAuthentication(authentication);
      return;
    }
    throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
  }

}
