package br.gov.es.openpmo.dto.workpackshared;

import br.gov.es.openpmo.model.office.Office;
import com.fasterxml.jackson.annotation.JsonCreator;

public class WorkpackSharedOfficeItem {

  private final Long id;
  private final String name;
  private final String fullName;

  @JsonCreator
  public WorkpackSharedOfficeItem(
    final Long id,
    final String name,
    final String fullName
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
  }

  public static WorkpackSharedOfficeItem of(final Office office) {
    return new WorkpackSharedOfficeItem(
      office.getId(),
      office.getName(),
      office.getFullName()
    );
  }

  public static WorkpackSharedOfficeItem ofDefault() {
    return new WorkpackSharedOfficeItem(
      null,
      "Todos",
      "All"
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }
}
