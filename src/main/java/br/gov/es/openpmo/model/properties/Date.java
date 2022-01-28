package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.DateModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.time.LocalDateTime;
import java.util.Objects;

@NodeEntity
public class Date extends Property<Date, LocalDateTime> {

    private LocalDateTime value;

    private CategoryEnum category;

    @Relationship(type = "COMPOSES")
    private Baseline baseline;

    @Relationship("FEATURES")
    private Workpack workpack;

    @Relationship("IS_DRIVEN_BY")
    private DateModel driver;

    @Override
    public Date snapshot() {
        final Date date = new Date();
        date.setValue(this.value);
        return date;
    }

    @Override
    public void setValue(final LocalDateTime value) {
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
    public LocalDateTime getValue() {
        return this.value;
    }

    public DateModel getDriver() {
        return this.driver;
    }

    public void setDriver(final DateModel driver) {
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
        final Date date = (Date) o;
        return Objects.equals(this.driver, date.driver);
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
    public boolean hasChanges(final Date other) {
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
