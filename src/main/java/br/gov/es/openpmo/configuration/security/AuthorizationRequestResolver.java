package br.gov.es.openpmo.configuration.security;

import br.gov.es.openpmo.configuration.properties.SpringSecurityProperties;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

public class AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

  private final OAuth2AuthorizationRequestResolver delegatedRequestResolver;
  
  private final SpringSecurityProperties springSecurityProperties;
  
  public AuthorizationRequestResolver(
    final ClientRegistrationRepository clientRegistrationRepository,
    final SpringSecurityProperties springSecurityProperties,
    final String authorizeUri
  ) {
    this.delegatedRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
      clientRegistrationRepository,
      authorizeUri
    );
    this.springSecurityProperties = springSecurityProperties;
  }

  @Override
  public OAuth2AuthorizationRequest resolve(final HttpServletRequest request) {
            
    final OAuth2AuthorizationRequest req = this.delegatedRequestResolver.resolve(request);
    return this.customizeRequest(req);
  }

  @Override
  public OAuth2AuthorizationRequest resolve(
    final HttpServletRequest request,
    final String clientRegistrationId
  ) {
    final OAuth2AuthorizationRequest req = this.delegatedRequestResolver.resolve(request, clientRegistrationId);
    return this.customizeRequest(req);
  }

  private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest request) {
    if(request == null) return null;
      
    String registrationId = (String) request.getAttributes().get(OAuth2ParameterNames.REGISTRATION_ID);
    
    Map<String, Object> wrapper = new HashMap();
    wrapper.put("s", request.getState());
    wrapper.put("rid", registrationId);
    
    String newState = Base64.getUrlEncoder().withoutPadding().encodeToString(JSONObject.valueToString(wrapper).getBytes());
    
    request = OAuth2AuthorizationRequest.from(request).state(newState).build();
       
    String extraParam = Optional.ofNullable(springSecurityProperties.getRegistration(registrationId))
                            .map(SpringSecurityProperties.Registration::getExtraResponseType).orElse(null);
    
    if(
        extraParam != null 
        && !extraParam.isEmpty()
    ){
        return OAuth2AuthorizationRequest.from(request).additionalParameters(this.additionalParams(request, extraParam)).build();
    
    }
    
    return request;
    

  }

  private Map<String, Object> additionalParams(final OAuth2AuthorizationRequest request, String extraParam) {
    final Map<String, Object> params = new HashMap<>(request.getAdditionalParameters());
    params.put(OAuth2ParameterNames.RESPONSE_TYPE, request.getResponseType().getValue() + " " + extraParam);
    return params;
  }

}
