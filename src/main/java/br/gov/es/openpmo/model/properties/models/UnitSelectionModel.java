package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.model.office.UnitMeasure;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class UnitSelectionModel extends PropertyModel {

  @Relationship("DEFAULTS_TO")
  UnitMeasure defaultValue;

  public UnitMeasure getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final UnitMeasure defaultValue) {
    this.defaultValue = defaultValue;
  }

}
