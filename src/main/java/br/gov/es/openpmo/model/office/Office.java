package br.gov.es.openpmo.model.office;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class Office extends Entity {

    private String name;
    private String fullName;

    @JsonIgnoreProperties("office")
    @Relationship(type = "IS_ADOPTED_BY", direction = INCOMING)
    private Set<Plan> plans;

    @JsonIgnoreProperties("office")
    @Relationship(type = "IS_ADOPTED_BY", direction = INCOMING)
    private Set<PlanModel> plansModel;

    @JsonIgnoreProperties("office")
    @Relationship(type = "IS_IN_CONTACT_BOOK_OF", direction = INCOMING)
    private Set<IsInContactBookOf> contactBooks;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public Set<Plan> getPlans() {
        return this.plans;
    }

    public void setPlans(final Set<Plan> plans) {
        this.plans = plans;
    }

    public Set<PlanModel> getPlansModel() {
        return this.plansModel;
    }

    public void setPlansModel(final Set<PlanModel> plansModel) {
        this.plansModel = plansModel;
    }

    public Set<IsInContactBookOf> getContactBooks() {
        return this.contactBooks;
    }

    public void setContactBooks(final Set<IsInContactBookOf> contactBooks) {
        this.contactBooks = contactBooks;
    }

    @Transient
    public OfficeDto getDto() {
        return new OfficeDto(this);
    }
}
