package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Order(1)
public class SecurityFilter extends OncePerRequestFilter {

  private static final Logger log = Logger.getLogger(SecurityFilter.class.getName());

  private final TokenService tokenService;

  @Autowired
  public SecurityFilter(final TokenService tokenService) {
    this.tokenService = tokenService;
  }

  private static boolean isPublicUrl(final String url) {
    return url.endsWith("/refresh")
           || url.endsWith("/signout")
           || (url.contains("/avatar") && url.endsWith("/person"))
           || url.endsWith("/versions")
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
      final String user = this.tokenService.getUser(token, TokenType.AUTHENTICATION).getSubject();
      final Authentication authentication = new UsernamePasswordAuthenticationToken(user, "");

      final SecurityContext securityContext = SecurityContextHolder.getContext();
      securityContext.setAuthentication(authentication);
      final HttpSession session = request.getSession(true);
      session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        securityContext
      );
      return;
    }
    throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
  }

}
