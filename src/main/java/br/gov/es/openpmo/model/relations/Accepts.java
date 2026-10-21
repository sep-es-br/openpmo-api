package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import java.util.Objects;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "ACCEPTS")
public class Accepts extends Entity {

  @Property("default")
  private Boolean defaultOption;

  @StartNode
  private CriteriaSelectionModel criteriaSelectionModel;

  @EndNode
  private SelectionOption selectionOption;

  public Boolean getDefaultOption() {
    return this.defaultOption;
  }

  public void setDefaultOption(final Boolean defaultOption) {
    this.defaultOption = defaultOption;
  }

  public CriteriaSelectionModel getCriteriaSelectionModel() {
    return this.criteriaSelectionModel;
  }

  public void setCriteriaSelectionModel(final CriteriaSelectionModel criteriaSelectionModel) {
    this.criteriaSelectionModel = criteriaSelectionModel;
  }

  public SelectionOption getSelectionOption() {
    return this.selectionOption;
  }

  public void setSelectionOption(final SelectionOption selectionOption) {
    this.selectionOption = selectionOption;
  }

  @Override
  public boolean equals(final Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || this.getClass() != object.getClass() || !super.equals(object)) {
      return false;
    }
    final Accepts accepts = (Accepts) object;
    return Objects.equals(this.defaultOption, accepts.defaultOption) &&
      Objects.equals(this.selectionOption, accepts.selectionOption);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.defaultOption, this.selectionOption);
  }

}
