package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.service.authentication.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final TokenService tokenService;

  private final ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  public SecurityConfiguration(
    final TokenService tokenService,
    final ClientRegistrationRepository clientRegistrationRepository
  ) {
    this.tokenService = tokenService;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }


  @Override
  protected void configure(final HttpSecurity httpSecurity) throws Exception {
    httpSecurity.cors().and().csrf().disable().authorizeRequests()
      .antMatchers("/signout").permitAll()
      .antMatchers("/signin").authenticated().and().oauth2Login()
      .authorizationEndpoint().authorizationRequestResolver(
        new AuthorizationRequestResolver(
          this.clientRegistrationRepository,
          "/oauth2/authorization"
        ))
    ;

    httpSecurity.addFilterBefore(this.securityFilter(), UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public SecurityFilter securityFilter() {
    return new SecurityFilter(this.tokenService);
  }

}
