package br.gov.es.openpmo.service.authentication;

import br.gov.es.openpmo.enumerator.TokenType;
import br.gov.es.openpmo.model.actors.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

  private static final String ISSUER = "OPENPMO-SEP";
  public static final String BEARER = "Bearer ";

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private String expiration;

  @Value("${jwt.refresh-secret}")
  private String refreshSecret;

  @Value("${jwt.refresh-expiration}")
  private String refreshExpiration;

  public String generateToken(
    final Person person,
    final String key,
    final String email,
    final TokenType tokenType
  ) {
    final String expirationValue = this.getExpirationValue(tokenType);
    final String secretValue = this.getSecretValue(tokenType);

    final Claims claims = Jwts.claims().setSubject(person.getId().toString());
    claims.put("key", key);
    claims.put("email", email);
    claims.put("administrator", person.getAdministrator());

    final Date today = new Date();
    final Date expirationDate = new Date(today.getTime() + Long.parseLong(expirationValue));

    return Jwts.builder()
      .setIssuer(ISSUER)
      .setIssuedAt(today)
      .setClaims(claims)
      .setExpiration(expirationDate)
      .signWith(SignatureAlgorithm.HS256, secretValue)
      .compact();
  }

  private String getExpirationValue(final TokenType tokenType) {
    if(TokenType.AUTHENTICATION.equals(tokenType)) {
      return this.expiration;
    }
    return this.refreshExpiration;
  }

  public boolean isValidToken(
    final String token,
    final TokenType tokenType
  ) {
    if(token == null) {
      return false;
    }
    try {
      final String secretValue = this.getSecretValue(tokenType);
      Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token);
      return true;
    }
    catch(final Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public Claims getUser(
    final String token,
    final TokenType tokenType
  ) {
    final String secretValue = this.getSecretValue(tokenType);
    return Jwts.parser()
      .setSigningKey(secretValue)
      .parseClaimsJws(token)
      .getBody();
  }

  public Long getPersonId(
    final String token,
    final TokenType tokenType
  ) {
    final String secretValue = this.getSecretValue(tokenType);
    final Claims claims = Jwts.parser()
      .setSigningKey(secretValue)
      .parseClaimsJws(token)
      .getBody();
    return Long.parseLong(claims.getSubject());
  }

  private String getSecretValue(final TokenType tokenType) {
    if(TokenType.AUTHENTICATION.equals(tokenType)) {
      return this.secret;
    }
    return this.refreshSecret;
  }

  public Long getUserId(final String authorization) {
    final String token = authorization.substring(BEARER.length());
    return this.getPersonId(token, TokenType.AUTHENTICATION);
  }

}
