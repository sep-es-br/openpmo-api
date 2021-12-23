package br.gov.es.openpmo.model.office;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class UnitMeasure extends Entity {

  private String name;

  private String fullName;

  private Long precision;

  @Relationship(type = "AVAILABLE_IN")
  private Office office;

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

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Long getPrecision() {
    return this.precision;
  }

  public void setPrecision(final Long precision) {
    this.precision = precision;
  }

}
