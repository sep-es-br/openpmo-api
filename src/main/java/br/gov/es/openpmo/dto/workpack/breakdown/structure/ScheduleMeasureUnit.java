package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import br.gov.es.openpmo.model.office.UnitMeasure;

public class ScheduleMeasureUnit {

  private final String name;

  private final String fullname;

  private final Long precision;

  public ScheduleMeasureUnit(
    final String name,
    final String fullname,
    final Long precision
  ) {
    this.name = name;
    this.fullname = fullname;
    this.precision = precision;
  }

  public static ScheduleMeasureUnit of(final UnitMeasure unitMeasure) {
    return new ScheduleMeasureUnit(
      unitMeasure.getName(),
      unitMeasure.getFullName(),
      unitMeasure.getPrecision()
    );
  }

  public String getName() {
    return this.name;
  }

  public String getFullname() {
    return this.fullname;
  }

  public Long getPrecision() {
    return this.precision;
  }

}
