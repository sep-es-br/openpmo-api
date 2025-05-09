package br.gov.es.openpmo.dto.plan;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;

public class PlanStoreDto {

  @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
  private Long idOffice;
  @NotNull(message = ApplicationMessage.PLANMODEL_NOT_NULL)
  private Long idPlanModel;
  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
  private String name;
  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  @Size(max = 600, message = "O nome completo deve ter no máximo 600 caracteres")
  private String fullName;
  @NotNull(message = ApplicationMessage.START_NOT_NULL)
  private LocalDate start;
  @NotNull(message = ApplicationMessage.FINISH_NOT_NULL)
  private LocalDate finish;

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public void setIdPlanModel(final Long idPlanModel) {
    this.idPlanModel = idPlanModel;
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

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getFinish() {
    return this.finish;
  }

  public void setFinish(final LocalDate finish) {
    this.finish = finish;
  }

}
