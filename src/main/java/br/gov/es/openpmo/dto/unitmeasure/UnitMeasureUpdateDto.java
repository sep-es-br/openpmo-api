package br.gov.es.openpmo.dto.unitmeasure;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UnitMeasureUpdateDto {
  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;
  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  private String name;
  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  private String fullName;
  @NotNull(message = ApplicationMessage.PRECISION_NOT_NULL)
  private Long precision;
  @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
  private Long idOffice;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
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

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public Long getPrecision() {
    return this.precision;
  }

  public void setPrecision(final Long precision) {
    this.precision = precision;
  }
}
