package br.gov.es.openpmo.enumerator;

public enum SimNao {

  SIM("S"),
  NAO("N");

  private final String valor;

  SimNao(final String valor) {
    this.valor = valor;
  }

  public String getValor() {
    return this.valor;
  }
}
