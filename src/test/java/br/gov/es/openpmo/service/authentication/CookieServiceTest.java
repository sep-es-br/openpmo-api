package br.gov.es.openpmo.service.authentication;

import javax.servlet.http.Cookie;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CookieServiceTest {

  private static final String COOKIE_NAME = "front_callback_url";
  private static final String COOKIE_VALUE = "https://pmo.es.gov.br";
  private static final String COOKIE_PATH = "/";

  private final CookieService service = new CookieService();

  @Test
  public void shouldCreateHostOnlyCookie() {
    final MockHttpServletResponse response = new MockHttpServletResponse();

    this.service.createCookie(response, COOKIE_NAME, COOKIE_VALUE, COOKIE_PATH);

    final Cookie cookie = response.getCookie(COOKIE_NAME);
    assertEquals(COOKIE_PATH, cookie.getPath());
    assertNull(cookie.getDomain());
    assertEquals(3600, cookie.getMaxAge());
    assertTrue(cookie.getSecure());
    assertTrue(cookie.isHttpOnly());
  }

  @Test
  public void shouldDeleteHostOnlyCookieWithTheOriginalPath() {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    request.setCookies(new Cookie(COOKIE_NAME, COOKIE_VALUE));

    this.service.deleteCookie(request, response, COOKIE_NAME, COOKIE_PATH);

    final Cookie cookie = response.getCookie(COOKIE_NAME);
    assertEquals(COOKIE_PATH, cookie.getPath());
    assertNull(cookie.getDomain());
    assertEquals(0, cookie.getMaxAge());
    assertTrue(cookie.getSecure());
    assertTrue(cookie.isHttpOnly());
  }

}
