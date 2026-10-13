package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TransversalViewSelectionModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class TransversalViewSelection extends Property<TransversalViewSelection, String> {

  private String value;

  private Long principalProgramId;

  private CategoryEnum category;

  @Relationship("IS_DRIVEN_BY")
  private TransversalViewSelectionModel driver;

  @Override
  public TransversalViewSelection snapshot() {
    final TransversalViewSelection selection = new TransversalViewSelection();
    selection.setValue(this.value);
    selection.setPrincipalProgramId(this.principalProgramId);
    return selection;
  }

  @Override
  public boolean hasChanges(final TransversalViewSelection other) {
    return !Objects.equals(this.value, other.value)
      || !Objects.equals(this.principalProgramId, other.principalProgramId);
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public void setValue(final String value) {
    this.value = value;
  }

  public Long getPrincipalProgramId() {
    return this.principalProgramId;
  }

  public void setPrincipalProgramId(final Long principalProgramId) {
    this.principalProgramId = principalProgramId;
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
  public PropertyModel getPropertyModel() {
    return this.driver;
  }

  public TransversalViewSelectionModel getDriver() {
    return this.driver;
  }

  public void setDriver(final TransversalViewSelectionModel driver) {
    this.driver = driver;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.driver);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    final TransversalViewSelection selection = (TransversalViewSelection) o;
    return Objects.equals(this.driver, selection.driver);
  }
}
