package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.CurrencyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.util.Objects;

@NodeEntity
public class Currency extends Property<Currency, BigDecimal> {

  private BigDecimal value;

  private CategoryEnum category;

  @Relationship("FEATURES")
  private Workpack workpack;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship("IS_DRIVEN_BY")
  private CurrencyModel driver;

  @Override
  public Currency snapshot() {
    final Currency currency = new Currency();
    currency.setValue(this.value);
    return currency;
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
  public boolean hasChanges(final Currency other) {
    return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
  }

  @Override
  public BigDecimal getValue() {
    return this.value;
  }

  @Override
  public void setValue(final BigDecimal value) {
    this.value = value;
  }

  public CurrencyModel getDriver() {
    return this.driver;
  }

  public void setDriver(final CurrencyModel driver) {
    this.driver = driver;
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
    final Currency currency = (Currency) o;
    return Objects.equals(this.driver, currency.driver);
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
