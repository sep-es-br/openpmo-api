package br.gov.es.openpmo.model.domain;

public enum SimNao {

    SIM("S"),
    NAO("N");

    private String valor;

    SimNao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
