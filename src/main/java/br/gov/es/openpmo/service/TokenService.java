package br.gov.es.openpmo.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.model.Person;
import br.gov.es.openpmo.model.domain.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

    private static final String ISSUER = "OPENPMO-SEP";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.refresh-expiration}")
    private String refreshExpiration;

    public String generateToken(Person person, TokenType tokenType) {
        String expirationValue = TokenType.AUTHENTICATION.equals(tokenType) ? expiration : refreshExpiration;
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        Date today = new Date();
        Date expirationDate = new Date(today.getTime() + Long.parseLong(expirationValue));
        Claims claims = Jwts.claims().setSubject(person.getId().toString());
        claims.put("email", person.getEmail());
        claims.put("administrator", person.isAdministrator());

        return Jwts.builder().setIssuer(ISSUER).setIssuedAt(today).setClaims(claims).setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretValue).compact();
    }

    public boolean isValidToken(String token, TokenType tokenType) {
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        if (token != null) {
            try {
                Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public Claims getUser(String token, TokenType tokenType) {
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        return Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token).getBody();
    }

    public Long getPersonId(String token, TokenType tokenType) {
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        Claims claims = Jwts.parser()
                            .setSigningKey(secretValue)
                            .parseClaimsJws(token)
                            .getBody();

        return Long.parseLong(claims.getSubject());
    }
}
