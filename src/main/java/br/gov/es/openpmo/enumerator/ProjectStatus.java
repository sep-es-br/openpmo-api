package br.gov.es.openpmo.enumerator;

import java.util.Arrays;

public enum ProjectStatus {
  ESTRUTURACAO("Estruturação"),
  EXECUCAO("Execução"),
  SUSPENSO("Suspenso"),
  CANCELADO("Cancelado"),
  CONCLUIDO("Concluído"),
  PARALISADO("Paralisado"),
  PLANEJAMENTO("Planejamento");

  private final String projectStatus;

  ProjectStatus(final String projectStatus) {
    this.projectStatus = projectStatus;
  }

  public String getProjectStatus() {
    return this.projectStatus;
  }

  public static ProjectStatus fromValue(String value) {
    return Arrays.stream(values())
      .filter(ps -> ps.projectStatus.equalsIgnoreCase(value))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException("Status inválido: " + value)
      );
  }
}
