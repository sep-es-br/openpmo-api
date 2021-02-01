package br.gov.es.openpmo.dto;

public class AcessoDto {

	private String token;
	private String refreshToken;
	private String tokenAux;

	public AcessoDto() {
	}

	public AcessoDto(String token, String refreshToken) {
		this.token = token;
		this.refreshToken = refreshToken;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenAux() {
		return tokenAux;
	}

	public void setTokenAux(String tokenAux) {
		this.tokenAux = tokenAux;
	}
}
