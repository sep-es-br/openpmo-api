/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.service.client.credential;

import br.gov.es.openpmo.configuration.properties.ClientCredentialProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class ClientCredentialService {
    
    private static final String SUFIX = ClientCredentialProperties.CLIENT_SUFIX;
    
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    
    public ClientCredentialService(
        final OAuth2AuthorizedClientManager authorizedClientManager
    ){
        this.authorizedClientManager = authorizedClientManager;
    }
    
    public String getClientToken(String registrationId) {
        
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(registrationId + SUFIX)
                                            .principal(new UsernamePasswordAuthenticationToken("System", "N/A"))
                                            .build();
        
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(request);
        
        return client.getAccessToken().getTokenValue();
                
    }
    
    
}
