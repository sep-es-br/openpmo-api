package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CriteriaGroup extends Property<CriteriaGroup, Set<Property>> {

  @Relationship("GROUPS")
  private Set<Property> groupedProperties;

  private CategoryEnum category;

  private boolean active = true;

  @Relationship("IS_DRIVEN_BY")
  private CriteriaGroupModel driver;

  @Override
  public CriteriaGroup snapshot() {
    final CriteriaGroup criteriaGroup = new CriteriaGroup();
    criteriaGroup.setValue(Optional.ofNullable(this.groupedProperties).map(HashSet::new).orElse(null));
    criteriaGroup.setActive(this.active);
    return criteriaGroup;
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
  public boolean hasChanges(final CriteriaGroup other) {
    final boolean propertiesChanged = (this.groupedProperties != null || other.groupedProperties != null)
      && (this.groupedProperties == null || !this.groupedProperties.equals(other.groupedProperties));
    return this.active != other.active || propertiesChanged;
  }

  @Override
  public Set<Property> getValue() {
    return this.groupedProperties;
  }

  @Override
  public void setValue(final Set<Property> value) {
    this.groupedProperties = value;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.driver;
  }

  public CriteriaGroupModel getDriver() {
    return this.driver;
  }

  public void setDriver(final CriteriaGroupModel driver) {
    this.driver = driver;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

}
