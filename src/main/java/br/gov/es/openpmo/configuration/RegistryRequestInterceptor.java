package br.gov.es.openpmo.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RegistryRequestInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryRequestInterceptor.class);

  private static String getAuthorization(final HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  private static void handleRequestBody(
    final ServletRequest request,
    final UUID uuid
  ) {
    try {
      final String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
      LOGGER.debug("[{}] | Request body: {}", uuid, body);
    }
    catch(final IOException e) {
      LOGGER.debug("[{}] | Error trying get request body", uuid);
    }
  }

  @Override
  public boolean preHandle(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final Object handler
  ) throws Exception {
    final UUID uuid = UUID.randomUUID();

    request.setAttribute("request-id", uuid.toString());

    final String params = Optional.ofNullable(request.getQueryString())
      .map(p -> "?" + p)
      .orElse("");

    LOGGER.debug(
      "[{}] | Request received: {} {}{}",
      uuid,
      request.getMethod(),
      request.getRequestURL(),
      params
    );
    LOGGER.debug("[{}] | Authorization: {}", uuid, getAuthorization(request));
    switch(request.getMethod()) {
      case "POST":
      case "PUT":
      case "PATCH":
//        handleRequestBody(request, uuid);
        break;
    }

    return true;
  }


}
