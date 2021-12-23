package br.gov.es.openpmo.dto.planmodel;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class PlanModelUpdateDto {
  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;
  @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
  private Long idOffice;
  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  private String name;
  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  private String fullName;

  private boolean sharedWithAll;
  private Set<OfficeDto> sharedWith;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public boolean isSharedWithAll() {
    return this.sharedWithAll;
  }

  public void setSharedWithAll(final boolean sharedWithAll) {
    this.sharedWithAll = sharedWithAll;
  }

  public Set<OfficeDto> getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Set<OfficeDto> sharedWith) {
    this.sharedWith = sharedWith;
  }
}
