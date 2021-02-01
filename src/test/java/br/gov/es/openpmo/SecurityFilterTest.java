package br.gov.es.openpmo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import br.gov.es.openpmo.configuration.security.SecurityFilter;
import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.domain.TokenType;
import br.gov.es.openpmo.service.TokenService;

import static org.mockito.Mockito.mockingDetails;


@SpringBootTest
public class SecurityFilterTest {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private TokenService tokenService;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private PrintWriter printWriter;
    private HttpSession httpSession;

    @BeforeEach
    public void init() throws IOException {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
        printWriter = Mockito.mock(PrintWriter.class);
        httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(response.getWriter()).thenReturn(printWriter);
        Mockito.when(request.getSession(true)).thenReturn(httpSession);
    }


    @Test
    public void shouldAuthorizePublicUrl() throws ServletException, IOException {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/documentation/index.html");
        securityFilter.doFilterInternal(request, response, filterChain);
        Collection<Invocation> invocations = mockingDetails(response).getInvocations();
        Assertions.assertNotNull(invocations);
        Assertions.assertTrue(invocations.isEmpty());
    }

    @Test
    public void shouldNotAuthorizeWithoutToken() throws ServletException, IOException {
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Mockito.when(request.getRequestURI()).thenReturn("/offices");
        securityFilter.doFilterInternal(request, response, filterChain);
        Collection<Invocation> invocations = mockingDetails(response).getInvocations();
        Assertions.assertNotNull(invocations);
        Assertions.assertFalse(invocations.isEmpty());
        Assertions.assertEquals(401, invocations.iterator().next().getArguments()[0]);
    }

    @Test
    public void shouldAuthorizeWithToken() throws ServletException, IOException {
        Person person = new Person();
        person.setId(1L);
        person.setEmail("user@openpmo.com");
        person.setName("User Test");
        String token = "Bearer " + tokenService.generateToken(person, TokenType.AUTHENTICATION);
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
        Mockito.when(request.getRequestURI()).thenReturn("/offices");
        securityFilter.doFilterInternal(request, response, filterChain);
        Collection<Invocation> invocations = mockingDetails(response).getInvocations();
        Assertions.assertNotNull(invocations);
        Assertions.assertTrue(invocations.isEmpty());
    }
}
