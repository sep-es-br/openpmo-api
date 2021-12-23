package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class Group extends Property<Group, Set<Property>> {

  @Relationship(type = "GROUPS")
  private Set<Property> groupedProperties;

  private CategoryEnum category;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship("FEATURES")
  private Workpack workpack;

  @Relationship("IS_DRIVEN_BY")
  private GroupModel driver;

  public Set<Property> getGroupedProperties() {
    return this.groupedProperties;
  }

  public void setGroupedProperties(final Set<Property> groupedProperties) {
    this.groupedProperties = groupedProperties;
  }

  public GroupModel getDriver() {
    return this.driver;
  }

  public void setDriver(final GroupModel driver) {
    this.driver = driver;
  }

  @Override
  public Group snapshot() {
    final Group group = new Group();
    group.setValue(Optional.ofNullable(this.groupedProperties).map(HashSet::new).orElse(null));
    return group;
  }

  @Override
  public Baseline getBaseline() {
    return this.baseline;
  }

  @Override
  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
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
  public boolean hasChanges(final Group other) {
    return (this.getValue() != null || other.getValue() != null)
           && (this.getValue() != null && other.getValue() == null || this.getValue() == null || !this.getValue().equals(other.getValue()));
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
  public Workpack getWorkpack() {
    return this.workpack;
  }

  @Override
  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

}
