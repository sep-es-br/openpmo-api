package br.gov.es.openpmo.dto;

public class ErroDto {

  private final String erro;

  public ErroDto(final String erro) {
    this.erro = erro;
  }

  public String getErro() {
    return this.erro;
  }

}
