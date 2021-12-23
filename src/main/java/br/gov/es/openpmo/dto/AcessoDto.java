package br.gov.es.openpmo.dto;

public class AcessoDto {

  private String token;
  private String refreshToken;
  private String tokenAux;

  public AcessoDto() {
  }

  public AcessoDto(final String token, final String refreshToken) {
    this.token = token;
    this.refreshToken = refreshToken;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public String getRefreshToken() {
    return this.refreshToken;
  }

  public void setRefreshToken(final String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTokenAux() {
    return this.tokenAux;
  }

  public void setTokenAux(final String tokenAux) {
    this.tokenAux = tokenAux;
  }
}
