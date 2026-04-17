/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.gov.es.openpmo.configuration.providers;

import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 *
 * @author gean.carneiro
 * @param <ID> tipo da identificação do usuario
 */
public interface BaseProvider<ID> {
        
    public ID getId(OAuth2User user, String token);
    
    public String getNome(OAuth2User user, String token);
    
    public String getEmail(OAuth2User user, String token);
    
    default Object getAttribute(OAuth2User user, String token, String attr) {
        return user.getAttribute(attr);
    }

    default <T> T getAttribute(OAuth2User user, String token, String attr, Class<T> type) {
        return type.cast(user.getAttribute(attr));
    }
    
        
}
