package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.configuration.security.SecurityFilter;
import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.service.authentication.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static org.mockito.Mockito.mockingDetails;


@SpringBootTest class SecurityFilterTest {

  @Autowired
  private SecurityFilter securityFilter;

  @Autowired
  private TokenService tokenService;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;
  private PrintWriter printWriter;
  private HttpSession httpSession;

  @BeforeEach void init() throws IOException {
    this.request = Mockito.mock(HttpServletRequest.class);
    this.response = Mockito.mock(HttpServletResponse.class);
    this.filterChain = Mockito.mock(FilterChain.class);
    this.printWriter = Mockito.mock(PrintWriter.class);
    this.httpSession = Mockito.mock(HttpSession.class);
    Mockito.when(this.response.getWriter()).thenReturn(this.printWriter);
    Mockito.when(this.request.getSession(true)).thenReturn(this.httpSession);
  }


  @Test void shouldAuthorizePublicUrl() throws ServletException, IOException {
    Mockito.when(this.request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
    Mockito.when(this.request.getRequestURI()).thenReturn("/documentation/index.html");
    this.securityFilter.doFilterInternal(this.request, this.response, this.filterChain);
    final Collection<Invocation> invocations = mockingDetails(this.response).getInvocations();
    Assertions.assertNotNull(invocations);
    Assertions.assertTrue(invocations.isEmpty());
  }

  @Test void shouldNotAuthorizeWithoutToken() throws ServletException, IOException {
    Mockito.when(this.request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
    Mockito.when(this.request.getRequestURI()).thenReturn("/offices");
    this.securityFilter.doFilterInternal(this.request, this.response, this.filterChain);
    final Collection<Invocation> invocations = mockingDetails(this.response).getInvocations();
    Assertions.assertNotNull(invocations);
    Assertions.assertFalse(invocations.isEmpty());
    Assertions.assertEquals(401, invocations.iterator().next().getArguments()[0]);
  }

  @Test void shouldAuthorizeWithToken() throws ServletException, IOException {
    final Person person = new Person();
    person.setId(1L);
    person.setName("User Test");
    final String token = "Bearer " + this.tokenService.generateToken(person, "email", TokenType.AUTHENTICATION);
    Mockito.when(this.request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
    Mockito.when(this.request.getRequestURI()).thenReturn("/offices");
    this.securityFilter.doFilterInternal(this.request, this.response, this.filterChain);
    final Collection<Invocation> invocations = mockingDetails(this.response).getInvocations();
    Assertions.assertNotNull(invocations);
    Assertions.assertTrue(invocations.isEmpty());
  }
}
