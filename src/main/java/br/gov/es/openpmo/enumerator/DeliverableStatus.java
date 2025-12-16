package br.gov.es.openpmo.enumerator;

import java.util.Arrays;

public enum DeliverableStatus {
  ACOES_PREPARATORIAS("Ações preparatórias"),
  PROJETO_EM_ELABORACAO("Projeto em elaboração"),
  PROJETO_ELABORADO("Projeto elaborado"),
  CONVENIO_ASSINADO("Convênio assinado"),
  EDITAL_PUBLICADO("Edital publicado"),
  LICITACAO_CONCLUIDA("Licitação concluída"),
  CONTRATO_ASSINADO("Contrato assinado"),
  OBRA_EM_ANDAMENTO("Obra em andamento"),
  SERVICO_EM_ANDAMENTO("Serviço em andamento"),
  CONCLUIDA("Concluída"),
  SUSPENSA("Suspensa"),
  EM_EXECUCAO("Em execução"),
  PARALISADO("Paralisado"),
  EM_LICITACAO("Em licitação"),
  CANCELADA("Cancelada"),
  A_LICITAR("A licitar"),
  A_CANCELAR("A cancelar");

  private final String deliverableStatus;

  DeliverableStatus(final String deliverableStatus) {
    this.deliverableStatus = deliverableStatus;
  }

  public String getDeliverableStatus() {
    return this.deliverableStatus;
  }

  public static DeliverableStatus fromValue(String value) {
    return Arrays.stream(values())
      .filter(ps -> ps.deliverableStatus.equalsIgnoreCase(value))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException("Status inválido: " + value)
      );
  }
}
