package br.gov.es.openpmo.model.obligations;

import br.gov.es.openpmo.dto.obligations.ObligationCreateDto;
import br.gov.es.openpmo.dto.obligations.ObligationUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

@NodeEntity
public class Obligation extends Entity {

    private String obligationNumber;

    private String description;
    private String managementUnitCode;

    @Relationship(type = "ISSUED_FOR")
    private Workpack workpack;

    public Obligation() {
    }

    public Obligation(
        final String obligationNumber,
        final String description,
        final String managementUnitCode,
        final Workpack workpack
    ) {
        this.obligationNumber = obligationNumber;
        this.description = description;
        this.managementUnitCode = managementUnitCode;
        this.workpack = workpack;
    }

    public static Obligation of(
        final ObligationCreateDto request,
        final Workpack workpack
    ) {
        return new Obligation(
            request.getObligationNumber(),
            request.getDescription(),
            request.getManagementUnitCode(),
            workpack
        );
    }

    public void update(final ObligationUpdateDto request) {
        ObjectUtils.updateIfPresent(
            request::getObligationNumber,
            this::setObligationNumber
        );

        ObjectUtils.updateIfPresent(
            request::getDescription,
            this::setDescription
        );
        ObjectUtils.updateIfPresent(request::getManagementUnitCode, this::setManagementUnitCode);
    }

    @Transient
    public Long getIdWorkpack() {
        return Optional.ofNullable(this.workpack)
            .map(Entity::getId)
            .orElse(null);
    }

    public String getObligationNumber() {
        return this.obligationNumber;
    }

    public void setObligationNumber(
        final String obligationNumber
    ) {
        this.obligationNumber = obligationNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(
        final String description
    ) {
        this.description = description;
    }

    public Workpack getWorkpack() {
        return this.workpack;
    }
    public String getManagementUnitCode() { return managementUnitCode; }
    public void setManagementUnitCode(String value) { managementUnitCode = value; }

    public void setWorkpack(final Workpack workpack) {
        this.workpack = workpack;
    }
}
