package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.properties.models.LocalitySelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Node
public class LocalitySelection extends Property<LocalitySelection, Set<Locality>> {

  @Relationship("VALUES")
  private Set<Locality> value;

  private CategoryEnum category;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship("FEATURES")
  private Workpack workpack;

  @Relationship("IS_DRIVEN_BY")
  private LocalitySelectionModel driver;

  public LocalitySelection() {
  }

  @Override
  public LocalitySelection snapshot() {
    final LocalitySelection localitySelection = new LocalitySelection();
    final HashSet<Locality> value = Optional.ofNullable(this.value).map(HashSet::new).orElse(null);
    localitySelection.setValue(value);
    return localitySelection;
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
  public boolean hasChanges(final LocalitySelection other) {
    return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
  }

  @Override
  public Set<Locality> getValue() {
    return this.value;
  }

  @Override
  public void setValue(final Set<Locality> value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.driver);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || this.getClass() != o.getClass()) {
      return false;
    }
    if(!super.equals(o)) {
      return false;
    }
    final LocalitySelection that = (LocalitySelection) o;
    return Objects.equals(this.driver, that.driver);
  }

  @Override
  public Workpack getWorkpack() {
    return this.workpack;
  }

  @Override
  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  @Override
  public PropertyModel getPropertyModel() {
    return this.getDriver();
  }

  public LocalitySelectionModel getDriver() {
    return this.driver;
  }

  public void setDriver(final LocalitySelectionModel driver) {
    this.driver = driver;
  }

}
