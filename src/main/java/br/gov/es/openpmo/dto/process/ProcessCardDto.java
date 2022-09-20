package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.model.process.Process;
import com.fasterxml.jackson.annotation.JsonCreator;

public class ProcessCardDto {

  private final Long id;
  private final String processNumber;
  private final String name;
  private final String currentOrganization;
  private final boolean priority;

  @JsonCreator
  public ProcessCardDto(
    final Long id,
    final String processNumber,
    final String name,
    final String currentOrganization,
    final boolean priority
  ) {
    this.id = id;
    this.processNumber = processNumber;
    this.name = name;
    this.currentOrganization = currentOrganization;
    this.priority = priority;
  }

  public static ProcessCardDto of(final Process process) {
    return new ProcessCardDto(
      process.getId(),
      process.getProcessNumber(),
      process.getName(),
      process.getCurrentOrganization(),
      process.getPriority()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getProcessNumber() {
    return this.processNumber;
  }

  public String getName() {
    return this.name;
  }

  public String getCurrentOrganization() {
    return this.currentOrganization;
  }

  public boolean isPriority() {
    return this.priority;
  }

}
