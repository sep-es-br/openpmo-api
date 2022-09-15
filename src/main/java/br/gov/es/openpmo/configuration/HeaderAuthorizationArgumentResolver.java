package br.gov.es.openpmo.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class HeaderAuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.hasParameterAnnotation(Authorization.class);
  }

  @Override
  public Object resolveArgument(
    final MethodParameter parameter,
    final ModelAndViewContainer mavContainer,
    final NativeWebRequest webRequest,
    final WebDataBinderFactory binderFactory
  ) throws Exception {
    final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    return request.getHeader("Authorization");
  }

}
