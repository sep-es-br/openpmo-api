package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CriteriaSelection extends Property<CriteriaSelection, Set<SelectionOption>> {

  @Relationship("VALUES")
  private Set<SelectionOption> value;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private CriteriaSelectionModel driver;

  @Override
  public CriteriaSelection snapshot() {
    final CriteriaSelection criteriaSelection = new CriteriaSelection();
    criteriaSelection.setValue(Optional.ofNullable(this.value).map(HashSet::new).orElse(null));
    return criteriaSelection;
  }

  @Override
  public CategoryEnum getCategory() {
    return this.category;
  }

  @Override
  public void setCategory(final CategoryEnum category) {
    this.category = category;
  }

  @Override
  public boolean hasChanges(final CriteriaSelection other) {
    return (this.value != null || other.value != null)
      && (this.value == null || !this.value.equals(other.value));
  }

  @Override
  public Set<SelectionOption> getValue() {
    return this.value;
  }

  @Override
  public void setValue(final Set<SelectionOption> value) {
    this.value = value;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.driver;
  }

  public CriteriaSelectionModel getDriver() {
    return this.driver;
  }

  public void setDriver(final CriteriaSelectionModel driver) {
    this.driver = driver;
  }

}
