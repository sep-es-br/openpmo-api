package br.gov.es.openpmo.dto.person.queries;


import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.*;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PersonPermissionDetailQuery {

    private final Person person;
    private final Office office;
    private final Plan plan;
    private final Workpack workpack;
    private final CanAccessOffice canAccessOffice;
    private final CanAccessPlan canAccessPlan;
    private final CanAccessWorkpack canAccessWorkpack;
    private final IsStakeholderIn isStakeholderIn;
    private final IsCCBMemberFor isCCBMemberFor;

    public PersonPermissionDetailQuery(
            Person person,
            Office office,
            Plan plan,
            Workpack workpack,
            CanAccessOffice canAccessOffice,
            CanAccessPlan canAccessPlan,
            CanAccessWorkpack canAccessWorkpack,
            IsStakeholderIn isStakeholderIn,
            IsCCBMemberFor isCCBMemberFor
    ) {
        this.person = person;
        this.office = office;
        this.plan = plan;
        this.workpack = workpack;
        this.canAccessOffice = canAccessOffice;
        this.canAccessPlan = canAccessPlan;
        this.canAccessWorkpack = canAccessWorkpack;
        this.isStakeholderIn = isStakeholderIn;
        this.isCCBMemberFor = isCCBMemberFor;
    }

    public Person getPerson() {
        return person;
    }

    public Office getOffice() {
        return office;
    }

    public CanAccessOffice getCanAccessOffice() {
        return canAccessOffice;
    }

    public CanAccessPlan getCanAccessPlan() {
        return canAccessPlan;
    }

    public CanAccessWorkpack getCanAccessWorkpack() {
        return canAccessWorkpack;
    }

    public IsStakeholderIn getIsStakeholderIn() {
        return isStakeholderIn;
    }

    public IsCCBMemberFor getIsCCBMemberFor() {
        return isCCBMemberFor;
    }

    public Plan getPlan() {
        return plan;
    }

    public Workpack getWorkpack() {
        return workpack;
    }
}
