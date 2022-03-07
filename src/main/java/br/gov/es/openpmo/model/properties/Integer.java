package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.IntegerModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Objects;

@NodeEntity
public class Integer extends Property<Integer, Long> {

    private Long value;

    private CategoryEnum category;

    @Relationship("FEATURES")
    private Workpack workpack;

    @Relationship(type = "COMPOSES")
    private Baseline baseline;

    @Relationship("IS_DRIVEN_BY")
    private IntegerModel driver;

    public Integer() {
    }

    @Override
    public Integer snapshot() {
        final Integer integer = new Integer();
        integer.setValue(this.value);
        return integer;
    }

    @Override
    public void setValue(final Long value) {
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
    public Long getValue() {
        return this.value;
    }

    public IntegerModel getDriver() {
        return this.driver;
    }

    public void setDriver(final IntegerModel driver) {
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
        final Integer integer = (Integer) o;
        return Objects.equals(this.driver, integer.driver);
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
    public boolean hasChanges(final Integer other) {
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
