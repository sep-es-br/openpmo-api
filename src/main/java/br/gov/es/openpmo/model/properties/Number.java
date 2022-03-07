package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.NumberModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Number extends Property<Number, Double> {

    private Double value;

    private CategoryEnum category;

    @Relationship("FEATURES")
    private Workpack workpack;

    @Relationship(type = "COMPOSES")
    private Baseline baseline;

    @Relationship("IS_DRIVEN_BY")
    private NumberModel driver;

    public Number() {
    }

    @Override
    public Number snapshot() {
        final Number number = new Number();
        number.setValue(this.value);
        return number;
    }

    @Override
    public void setValue(final Double value) {
        this.value = value;
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
    public Double getValue() {
        return this.value;
    }

    public NumberModel getDriver() {
        return this.driver;
    }

    public void setDriver(final NumberModel driver) {
        this.driver = driver;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final Number number = (Number) o;
        return Objects.equals(this.driver, number.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.driver);
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
    public boolean hasChanges(final Number other) {
        return (this.value != null || other.value != null)
                && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
    }

    @Override
    public Baseline getBaseline() {
        return this.baseline;
    }

    @Override
    public void setBaseline(final Baseline baseline) {
        this.baseline = baseline;
    }

}
