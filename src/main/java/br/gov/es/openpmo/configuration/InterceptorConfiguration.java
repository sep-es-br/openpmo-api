package br.gov.es.openpmo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorConfiguration implements WebMvcConfigurer {

  private final RegistryRequestInterceptor registryRequestInterceptor;

  @Autowired
  public InterceptorConfiguration(final RegistryRequestInterceptor registryRequestInterceptor) {
    this.registryRequestInterceptor = registryRequestInterceptor;
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(this.registryRequestInterceptor);
    WebMvcConfigurer.super.addInterceptors(registry);
  }

}
