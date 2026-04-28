package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.service.authentication.CookieService;
import br.gov.es.openpmo.utils.ConverterStringUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OAuth2AuthorizationFilter extends OncePerRequestFilter {

  private static final String FRONT_CALLBACK_URL = "front_callback_url";
  @Autowired
  private CookieService cookieService;
  @Autowired
  private ConverterStringUtils converterUtil;
  
  @Override
  public void destroy() {
    //Método não implementado por não haver necessidade.
  }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(request.getRequestURI().contains("/oauth2/authorization/")) {
          if(request.getQueryString() != null && !request.getQueryString().isEmpty()) {
              
            String frontCallback = request.getParameter(FRONT_CALLBACK_URL);
            
            String registrationId = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
            
            request.getSession(true).setAttribute("OAUTH2_PROVIDER", registrationId);

            if (frontCallback != null) {
                cookieService.createCookie(
                    response,
                    FRONT_CALLBACK_URL,
                    frontCallback,
                    "/"
                );
            }

          }
        }
            
        chain.doFilter(request, response);
    }

}
