/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.configuration.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author gean.carneiro
 */
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class ClientCredentialProperties {
    
    public static final String CLIENT_SUFIX = "-client";
    
    private final Map<String, Registration> registration;
    private final Map<String, Provider> provider;

    public ClientCredentialProperties(
        final Map<String, Registration> registration,
        final Map<String, Provider> provider
    ) {
        this.registration = registration;
        this.provider = provider;
    }
    
    public Registration getRegistration(String registrationId){
        
        Registration registration = this.registration.get(registrationId + CLIENT_SUFIX);
        
        if(registration == null) {
            throw new IllegalStateException("Client Credential não configurado: " + registrationId);
        }
        
        return registration;
    }
    
    
    public Provider getProvider(String registrationId){
        
        Provider provider = this.provider.get(registrationId + CLIENT_SUFIX);
        
        if(provider == null) {
            throw new IllegalStateException("Client Credential não configurado: " + registrationId);
        }
        
        return provider;
    }
    
    
    public static class Registration {
        private String clientId;
        private String clientSecret;
        private String authorizationGrantType;
        private String scope;

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getAuthorizationGrantType() {
            return authorizationGrantType;
        }

        public void setAuthorizationGrantType(String authorizationGrantType) {
            this.authorizationGrantType = authorizationGrantType;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
        
        
    }
    
    public static class Provider {
        private String tokenUri;
        private String webapi;

        public String getTokenUri() {
            return tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }

        public String getWebapi() {
            return webapi;
        }

        public void setWebapi(String webapi) {
            this.webapi = webapi;
        }
        
        
    }
    
}
