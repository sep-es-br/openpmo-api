package br.gov.es.openpmo.enumerator;

public enum GeneralOperatorsEnum {
  IGUAL("="), DIFERENTE("<>"), MAIOR(">"), MENOR("<"), MAIOR_IGUAL(">="), MENOR_IGUAL("<=");

  private final String operador;

  GeneralOperatorsEnum(final String operador) {
    this.operador = operador;
  }

  public String getOperador() {
    return this.operador;
  }
}
