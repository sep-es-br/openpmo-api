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
public class SpringSecurityProperties {
    
    private Map<String, Registration> registration;


    public Map<String, Registration> getRegistration() {
        return registration;
    }

    public void setRegistration(Map<String, Registration> registration) {
        this.registration = registration;
    }
    
    public Registration getRegistration(String registrationId) {
        return this.registration.get(registrationId);
    }
            
   public static class Registration {
       
       private String extraResponseType;
       private String clientId;
       private String scope;
       private String clientSecret;
       private String webapi;
       private String tokenUri;

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getWebapi() {
            return webapi;
        }

        public void setWebapi(String webapi) {
            this.webapi = webapi;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public void setTokenUri(String tokenUri) {
            this.tokenUri = tokenUri;
        }
       
       

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
       
       

        public void setExtraResponseType(String extraResponseType) {
            this.extraResponseType = extraResponseType;
        }
       
       

        public String getExtraResponseType() {
            return extraResponseType;
        }
       
       
   }
    
}
