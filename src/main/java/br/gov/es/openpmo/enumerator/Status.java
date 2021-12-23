package br.gov.es.openpmo.enumerator;

public enum Status {
  ATIVO("A"),
  INATIVO("I");

  private final String valor;

  Status(final String valor) {
    this.valor = valor;
  }

  public String getValor() {
    return this.valor;
  }
}
