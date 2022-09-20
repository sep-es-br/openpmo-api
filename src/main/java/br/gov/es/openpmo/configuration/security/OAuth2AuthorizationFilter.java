package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.service.authentication.CookieService;
import br.gov.es.openpmo.utils.ConverterStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@WebFilter(urlPatterns = "/oauth2/authorization/idsvr")
@Order(-150)
public class OAuth2AuthorizationFilter implements Filter {

  private static final String FRONT_CALLBACK_URL = "front_callback_url";
  @Autowired
  private CookieService cookieService;
  @Autowired
  private ConverterStringUtils converterUtil;

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    //Método não implementado.
  }

  @Override
  public void doFilter(
    final ServletRequest servletRequest,
    final ServletResponse servletResponse,
    final FilterChain chain
  )
    throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) servletRequest;
    final HttpServletResponse response = (HttpServletResponse) servletResponse;

    if(request.getRequestURI().contains("/oauth2/authorization/idsvr")) {
      if(request.getQueryString() != null && !request.getQueryString().isEmpty()) {
        final Map<String, String> params = this.converterUtil.convertQueryStringToHashMap(request.getQueryString());
        if(params.get(FRONT_CALLBACK_URL) != null) {
          this.cookieService.createCookie(response, FRONT_CALLBACK_URL, params.get(FRONT_CALLBACK_URL),
                                          "/openpmo"
          );
        }

        response.sendRedirect(request.getRequestURI());
      }
      else {
        chain.doFilter(servletRequest, servletResponse);
      }
    }
    else {
      chain.doFilter(servletRequest, servletResponse);
    }
  }

  @Override
  public void destroy() {
    //Método não implementado por não haver necessidade.
  }

}
