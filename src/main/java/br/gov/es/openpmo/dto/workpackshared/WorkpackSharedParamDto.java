package br.gov.es.openpmo.dto.workpackshared;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import com.fasterxml.jackson.annotation.JsonCreator;

public class WorkpackSharedParamDto {

  private final Long id;
  private final WorkpackSharedOfficeItem office;
  private final PermissionLevelEnum level;

  @JsonCreator
  public WorkpackSharedParamDto(
    final Long id,
    final WorkpackSharedOfficeItem office,
    final PermissionLevelEnum level
  ) {
    this.id = id;
    this.office = office;
    this.level = level;
  }

  public String officeName() {
    return this.office.getFullName();
  }

  public PermissionLevelEnum getLevel() {
    return this.level;
  }

  public Long getId() {
    return this.id;
  }

  public Long idOffice() {
    return this.office.getId();
  }

  public WorkpackSharedOfficeItem getOffice() {
    return this.office;
  }

}
