package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class OrganizationSelection extends Property<OrganizationSelection, Set<Organization>> {

    @Relationship("VALUES")
    private Set<Organization> value;

    private CategoryEnum category;

    @Relationship("FEATURES")
    private Workpack workpack;

    @Relationship(type = "COMPOSES")
    private Baseline baseline;

    @Relationship("IS_DRIVEN_BY")
    private OrganizationSelectionModel driver;

    public OrganizationSelection() {
    }

    @Override
    public OrganizationSelection snapshot() {
        final OrganizationSelection organizationSelection = new OrganizationSelection();
        organizationSelection.setValue(Optional.ofNullable(this.value).map(HashSet::new).orElse(null));
        return organizationSelection;
    }

    @Override
    public void setValue(final Set<Organization> value) {
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
    public Set<Organization> getValue() {
        return this.value;
    }

    public OrganizationSelectionModel getDriver() {
        return this.driver;
    }

    public void setDriver(final OrganizationSelectionModel driver) {
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
        final OrganizationSelection that = (OrganizationSelection) o;
        return Objects.equals(this.driver, that.driver);
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
    public boolean hasChanges(final OrganizationSelection other) {
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
