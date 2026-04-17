/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.configuration.providers;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component("google")
public class GoogleProvider implements BaseProvider<String> {

    private final String SUB = "sub";
    private final String NOME = "name";
    private final String EMAIL = "email";
    
    @Override
    public String getId(OAuth2User user, String token) {
        return (String) user.getAttribute(SUB);
    }

    @Override
    public String getNome(OAuth2User user, String token) {
        return (String) user.getAttribute(NOME);
    }

    @Override
    public String getEmail(OAuth2User user, String token) {
        return (String) user.getAttribute(EMAIL);
    }
    
    
    
}
