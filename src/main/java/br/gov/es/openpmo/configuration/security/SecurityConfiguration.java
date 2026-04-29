package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.configuration.properties.SpringSecurityProperties;
import br.gov.es.openpmo.service.authentication.TokenService;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    private static final String FRONT_CALLBACK_URL = "front_callback_url";
    
  private final TokenService tokenService;
  
  @Autowired
  private SecurityFilter securityFilter;
  
  @Autowired
  private OAuth2AuthorizationFilter oAuth2AuthorizationFilter;
  
  @Autowired
  private OAuth2LoginSuccessHandler auth2LoginSuccessHandler;
  
  @Autowired
  private SpringSecurityProperties springSecurityProperties;
    
  private final ClientRegistrationRepository clientRegistrationRepository;
    
  private final String frontendUrl;

  @Autowired
  public SecurityConfiguration(
    final TokenService tokenService,
    final ClientRegistrationRepository clientRegistrationRepository,
    @Value("${app.homeURI}") final String frontendUrl
  ) {
    this.tokenService = tokenService;
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.frontendUrl = frontendUrl;
  }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {

      http
        .cors().and()
        .csrf().disable()
              
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and()
        .formLogin().disable()
        .httpBasic().disable()
              
        .authorizeRequests()
          .antMatchers("/signout").permitAll()
          .antMatchers("/acesso-cidadao-response.html", "/signin/**").permitAll()
          .anyRequest().authenticated()

        .and()
        .oauth2Login(oauth -> oauth
            .successHandler(this.auth2LoginSuccessHandler)
            .authorizationEndpoint().authorizationRequestResolver( new AuthorizationRequestResolver( this.clientRegistrationRepository, springSecurityProperties, "/oauth2/authorization" ))
        )
        .logout(logout -> logout
                            .logoutUrl("/logout")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .logoutSuccessUrl(this.frontendUrl + "/login"))
        
        .exceptionHandling().authenticationEntryPoint((req, res, ex) -> {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"unauthorized\"}");
        });

      http.addFilterBefore(this.securityFilter, UsernamePasswordAuthenticationFilter.class);
      http.addFilterBefore(this.oAuth2AuthorizationFilter, OAuth2AuthorizationRequestRedirectFilter.class);
    }
    
  
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
      
    config.setAllowedOrigins(Arrays.asList(this.frontendUrl));
    config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
  
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository registrations,
            OAuth2AuthorizedClientService clientService
    ) {

        OAuth2AuthorizedClientProvider provider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        registrations,
                        clientService
                );

        manager.setAuthorizedClientProvider(provider);

        return manager;
    }
  

}
