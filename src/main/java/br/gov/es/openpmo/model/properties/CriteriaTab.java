package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CriteriaTab extends Property<CriteriaTab, Set<Property>> {

  @Relationship("ORGANIZES")
  private Set<Property> organizedProperties;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private CriteriaTabModel driver;

  @Override
  public CriteriaTab snapshot() {
    final CriteriaTab criteriaTab = new CriteriaTab();
    criteriaTab.setValue(Optional.ofNullable(this.organizedProperties).map(HashSet::new).orElse(null));
    return criteriaTab;
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
  public boolean hasChanges(final CriteriaTab other) {
    return (this.organizedProperties != null || other.organizedProperties != null)
      && (this.organizedProperties == null || !this.organizedProperties.equals(other.organizedProperties));
  }

  @Override
  public Set<Property> getValue() {
    return this.organizedProperties;
  }

  @Override
  public void setValue(final Set<Property> value) {
    this.organizedProperties = value;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.driver;
  }

  public CriteriaTabModel getDriver() {
    return this.driver;
  }

  public void setDriver(final CriteriaTabModel driver) {
    this.driver = driver;
  }

}
