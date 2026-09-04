package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.utils.ApplicationMessage;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreatePreProjectRequest {

  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  @Size(max = 50)
  private String name;

  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  @Size(max = 600)
  private String fullName;

  @NotNull
  private Long idOffice;

  @NotNull
  private Long idOrganization;

  private LocalDate expectedCompletionDate;

  private String expectedDeliveries;

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

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public Long getIdOrganization() {
    return this.idOrganization;
  }

  public void setIdOrganization(final Long idOrganization) {
    this.idOrganization = idOrganization;
  }

  public LocalDate getExpectedCompletionDate() {
    return this.expectedCompletionDate;
  }

  public void setExpectedCompletionDate(final LocalDate expectedCompletionDate) {
    this.expectedCompletionDate = expectedCompletionDate;
  }

  public String getExpectedDeliveries() {
    return this.expectedDeliveries;
  }

  public void setExpectedDeliveries(final String expectedDeliveries) {
    this.expectedDeliveries = expectedDeliveries;
  }

}
