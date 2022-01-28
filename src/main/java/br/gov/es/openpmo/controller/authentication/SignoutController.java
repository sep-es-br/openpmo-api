package br.gov.es.openpmo.controller.authentication;

import br.gov.es.openpmo.service.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/signout")
public class SignoutController {

  private final AuthenticationService authenticationService;

  @Autowired
  public SignoutController(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @GetMapping
  public void index(@RequestParam("token") final String authorization, final HttpServletRequest request,
                    final HttpServletResponse response) throws IOException {

    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if(auth != null) {
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }
    SecurityContextHolder.getContext().setAuthentication(null);
    this.authenticationService.signOut(authorization, response);
  }

}
