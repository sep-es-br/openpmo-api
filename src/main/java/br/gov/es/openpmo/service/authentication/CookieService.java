package br.gov.es.openpmo.service.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CookieService {

  @Value("${app.domain.cookie}")
  private String domainCookie;


  public void createCookie(
    final HttpServletResponse response,
    final String name,
    final String value,
    final String path
  ) {
    final Cookie cookie = new Cookie(name, value);
    cookie.setMaxAge(3600);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath(path);
    cookie.setDomain(this.domainCookie);
    response.addCookie(cookie);
  }

  public void deleteCookie(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final String name,
    final String path
  ) {
    final Cookie cookie = this.findCookie(request, name);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    cookie.setPath(path);
    cookie.setDomain(this.domainCookie);
    response.addCookie(cookie);
  }

  public Cookie findCookie(
    final HttpServletRequest request,
    final String name
  ) {
    if(request.getCookies() != null) {
      final Optional<Cookie> filter = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals(name)).findAny();
      if(!filter.isPresent()) {
        throw new IllegalArgumentException("Cookie ".concat(name).concat(" not found."));
      }
      else {
        return filter.get();
      }
    }
    throw new IllegalArgumentException("Cookie ".concat(name).concat(" not found."));
  }

}
