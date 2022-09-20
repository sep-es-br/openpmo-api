package br.gov.es.openpmo.dto.planmodel;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PlanModelStoreDto {

  @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
  private final Long idOffice;
  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  private final String name;
  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  private final String fullName;

  private final boolean sharedWithAll;
  private final Set<OfficeDto> sharedWith;

  public PlanModelStoreDto(
    final Long idOffice,
    final String name,
    final String fullName,
    final boolean sharedWithAll,
    final Set<OfficeDto> sharedWith
  ) {
    this.idOffice = idOffice;
    this.name = name;
    this.fullName = fullName;
    this.sharedWithAll = sharedWithAll;
    this.sharedWith = sharedWith;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public boolean isSharedWithAll() {
    return this.sharedWithAll;
  }

  public Set<OfficeDto> getSharedWith() {
    return this.sharedWith;
  }

}
