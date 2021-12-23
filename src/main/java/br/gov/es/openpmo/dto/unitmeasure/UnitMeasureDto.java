package br.gov.es.openpmo.dto.unitmeasure;

import br.gov.es.openpmo.model.office.UnitMeasure;

public class UnitMeasureDto {
  private Long id;
  private String name;
  private String fullName;
  private Long precision;
  private Long idOffice;

  public UnitMeasureDto() {

  }

  public UnitMeasureDto(final UnitMeasure unitMeasure) {
    this.id = unitMeasure.getId();
    this.name = unitMeasure.getName();
    this.fullName = unitMeasure.getFullName();
    this.idOffice = unitMeasure.getOffice().getId();
    this.precision = unitMeasure.getPrecision();
  }

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
