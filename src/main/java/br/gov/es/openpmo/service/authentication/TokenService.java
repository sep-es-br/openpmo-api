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

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private String expiration;

  @Value("${jwt.refresh-secret}")
  private String refreshSecret;

  @Value("${jwt.refresh-expiration}")
  private String refreshExpiration;

  public String generateToken(final Person person, final String email, final TokenType tokenType) {
    final String expirationValue = TokenType.AUTHENTICATION.equals(tokenType) ? this.expiration : this.refreshExpiration;
    final String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? this.secret : this.refreshSecret;

    final Date today = new Date();
    final Date expirationDate = new Date(today.getTime() + Long.parseLong(expirationValue));
    final Claims claims = Jwts.claims().setSubject(person.getId().toString());
    claims.put("email", email);
    claims.put("administrator", person.getAdministrator());

    return Jwts.builder().setIssuer(ISSUER).setIssuedAt(today).setClaims(claims).setExpiration(expirationDate)
      .signWith(SignatureAlgorithm.HS256, secretValue).compact();
  }

  public boolean isValidToken(final String token, final TokenType tokenType) {
    final String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? this.secret : this.refreshSecret;

    if(token != null) {
      try {
        Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token);
        return true;
      }
      catch(final Exception e) {
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  public Claims getUser(final String token, final TokenType tokenType) {
    final String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? this.secret : this.refreshSecret;

    return Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token).getBody();
  }

  public Long getPersonId(final String token, final TokenType tokenType) {
    final String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? this.secret : this.refreshSecret;

    final Claims claims = Jwts.parser()
      .setSigningKey(secretValue)
      .parseClaimsJws(token)
      .getBody();

    return Long.parseLong(claims.getSubject());
  }

  public Long getUserId(final String authorization) {
    final String token = authorization.substring(7);
    return this.getPersonId(token, TokenType.AUTHENTICATION);
  }
}
