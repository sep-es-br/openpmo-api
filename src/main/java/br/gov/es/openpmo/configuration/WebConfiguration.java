package br.gov.es.openpmo.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    final ObjectMapper mapper = new ObjectMapper();

    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
    mapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));

    mapper.registerModule(new ParameterNamesModule())
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));

    return new MappingJackson2HttpMessageConverter(mapper);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public Logger logger() {
    return LoggerFactory.getLogger("br.gov.es.openpmo");
  }

  @Override
  public void addFormatters(final FormatterRegistry registry) {
    registry.addConverter(new YearMonthConverter());
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    if(!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/");
    }
    registry.addResourceHandler("/documentation/**")
      .addResourceLocations("classpath:/documentation/");
  }

  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD", "TRACE", "CONNECT");
  }

  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new HeaderAuthorizationArgumentResolver());
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
  }

}
