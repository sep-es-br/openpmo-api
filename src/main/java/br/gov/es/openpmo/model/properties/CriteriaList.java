package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CriteriaList extends Property<CriteriaList, Set<ListItem>> {

  @Relationship("CONTAINS")
  private Set<ListItem> items;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private CriteriaListModel driver;

  @Override
  public CriteriaList snapshot() {
    final CriteriaList criteriaList = new CriteriaList();
    criteriaList.setValue(Optional.ofNullable(this.items).map(HashSet::new).orElse(null));
    return criteriaList;
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
  public boolean hasChanges(final CriteriaList other) {
    return (this.items != null || other.items != null)
      && (this.items == null || !this.items.equals(other.items));
  }

  @Override
  public Set<ListItem> getValue() {
    return this.items;
  }

  @Override
  public void setValue(final Set<ListItem> value) {
    this.items = value;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.driver;
  }

  public CriteriaListModel getDriver() {
    return this.driver;
  }

  public void setDriver(final CriteriaListModel driver) {
    this.driver = driver;
  }

}
