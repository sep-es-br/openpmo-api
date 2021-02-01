package br.gov.es.openpmo.model.domain;

public enum Status {
    ATIVO("A"),
    INATIVO("I");

    private String valor;

    Status(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
