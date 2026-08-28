package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.budget.FinancialSource;
import br.gov.es.openpmo.model.properties.models.FinancialSourceSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class FinancialSourceSelection extends Property<FinancialSourceSelection, Set<FinancialSource>> {

  @Relationship("VALUES")
  private Set<FinancialSource> value;

  @Relationship("IS_DRIVEN_BY")
  private FinancialSourceSelectionModel driver;

  private CategoryEnum category;

  @Override
  public Set<FinancialSource> getValue() {
    return value;
  }

  @Override
  public void setValue(Set<FinancialSource> value) {
    this.value = value;
  }

  public FinancialSourceSelectionModel getDriver() {
    return driver;
  }

  public void setDriver(FinancialSourceSelectionModel driver) {
    this.driver = driver;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return driver;
  }

  @Override
  public CategoryEnum getCategory() {
    return category;
  }

  @Override
  public void setCategory(CategoryEnum category) {
    this.category = category;
  }

  @Override
  public FinancialSourceSelection snapshot() {
    FinancialSourceSelection snapshot = new FinancialSourceSelection();
    snapshot.setValue(Optional.ofNullable(value).map(HashSet::new).orElse(null));
    return snapshot;
  }

  @Override
  public boolean hasChanges(FinancialSourceSelection other) {
    return (value != null || other.value != null)
      && (value != null && other.value == null || value == null || !value.equals(other.value));
  }
}
