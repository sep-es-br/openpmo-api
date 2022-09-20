package br.gov.es.openpmo.dto.workpackshared;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.workpacks.Workpack;

public class WorkpackSharedDto {

  private Long id;
  private WorkpackSharedOfficeItem office;
  private PermissionLevelEnum level;

  public WorkpackSharedDto() {
  }

  public WorkpackSharedDto(
    final Long id,
    final WorkpackSharedOfficeItem office,
    final PermissionLevelEnum level
  ) {
    this.id = id;
    this.office = office;
    this.level = level;
  }

  public static WorkpackSharedDto of(final Workpack workpack) {
    return new WorkpackSharedDto(
      workpack.getId(),
      WorkpackSharedOfficeItem.ofDefault(),
      workpack.getPublicLevel()
    );
  }

  public static WorkpackSharedDto of(final IsSharedWith isSharedWith) {
    return new WorkpackSharedDto(
      isSharedWith.getId(),
      WorkpackSharedOfficeItem.of(isSharedWith.getOffice()),
      isSharedWith.getPermissionLevel()
    );
  }

  public WorkpackSharedOfficeItem getOffice() {
    return this.office;
  }

  public void setOffice(final WorkpackSharedOfficeItem office) {
    this.office = office;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public PermissionLevelEnum getLevel() {
    return this.level;
  }

  public void setLevel(final PermissionLevelEnum level) {
    this.level = level;
  }

}
