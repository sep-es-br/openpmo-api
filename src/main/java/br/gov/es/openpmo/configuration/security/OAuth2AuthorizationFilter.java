package br.gov.es.openpmo.configuration.security;

import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.gov.es.openpmo.service.CookieService;
import br.gov.es.openpmo.utils.ConverterStringUtils;

@Component
@WebFilter(urlPatterns = { "/oauth2/authorization/idsvr" })
@Order(-150)
public class OAuth2AuthorizationFilter implements Filter {

    @Autowired
    private CookieService cookieService;

    @Autowired
    private ConverterStringUtils converterUtil;

    private static final String FRONT_CALLBACK_URL = "front_callback_url";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //Método não implementado.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getRequestURI().contains("/oauth2/authorization/idsvr")) {
            if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
                Map<String, String> params = converterUtil.convertQueryStringToHashMap(request.getQueryString());
                if (params.get(FRONT_CALLBACK_URL) != null) {
                    cookieService.createCookie(response, FRONT_CALLBACK_URL, params.get(FRONT_CALLBACK_URL),
                            "/openpmo");
                }

                response.sendRedirect(request.getRequestURI());
            } else {
                chain.doFilter(servletRequest, servletResponse);
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        //Método não implementado por não haver necessidade.
    }
}
