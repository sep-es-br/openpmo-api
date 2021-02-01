package br.gov.es.openpmo.configuration.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import br.gov.es.openpmo.dto.ErroDto;
import br.gov.es.openpmo.exception.AutenticacaoException;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.TokenService;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Order(1)
public class SecurityFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    static Logger log = Logger.getLogger(SecurityFilter.class.getName());

    @Autowired
    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String url = request.getRequestURI();

        boolean isSwaggerUrl = url.endsWith("/documentation/index.html") || url.endsWith("/v2/api-docs")
            || url.contains("/swagger-resources") || url.contains("/swagger-ui-standalone-preset.js")
            || url.contains("/swagger-ui-bundle.js") || url.contains("/swagger-ui.css")
            || url.contains("/favicon-16x16.ico") || url.contains("/logo.png") || url.contains("/webjars/");

        boolean isPublicUrl = url.endsWith("/signin/refresh") || url.endsWith("/signout")
            || url.endsWith("/acesso-cidadao-response.html") || url.endsWith("/acesso-cidadao-response");


        if (isPublicUrl || isSwaggerUrl) {
            filterChain.doFilter(request, response);
        } else {
            try {
                validateToken(request);
            } catch (AutenticacaoException a) {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                ErroDto erroDto = new ErroDto(a.getMessage());
                JSONObject erroJON = new JSONObject(erroDto);
                response.setCharacterEncoding("utf-8");
                response.getWriter().write(erroJON.toString());
                return;
            } catch (Exception e) {
                log.log(Level.SEVERE, e.toString(), e);
                throw e;
            }
            filterChain.doFilter(request, response);
        }

    }

    private void validateToken(HttpServletRequest request) {
        String token = getToken(request).split(" ")[1];
        if (tokenService.isValidToken(token, TokenType.AUTHENTICATION)) {
            String user = tokenService.getUser(token, TokenType.AUTHENTICATION).getSubject();
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, "");

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                                 securityContext);
            return;
        }
        throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String url = request.getRequestURI();
        if (token == null && url.endsWith("/signout")) {
            token = request.getParameter("token");
        }

        if (token == null) {
            throw new AutenticacaoException(ApplicationMessage.TOKEN_NOT_FOUND);
        }

        if (!token.startsWith("Bearer ")) {
            throw new AutenticacaoException(ApplicationMessage.INVALID_TOKEN);
        }

        return token;
    }
}
