package br.gov.es.openpmo.model.procurements;

import br.gov.es.openpmo.dto.procurements.ProcurementCreateDto;
import br.gov.es.openpmo.dto.procurements.ProcurementUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

@NodeEntity
public class Procurement extends Entity {

    private Long processId;
    private String object;
    private String organizationIdentifier;

    @Relationship(type = "RELATED_TO")
    private Workpack workpack;

    public Procurement() {
    }

    public Procurement(
        final Long processId,
        final String object,
        final String organizationIdentifier,
        final Workpack workpack
    ) {
        this.processId = processId;
        this.object = object;
        this.organizationIdentifier = organizationIdentifier;
        this.workpack = workpack;
    }

    public static Procurement of(
        final ProcurementCreateDto request,
        final Workpack workpack
    ) {
        return new Procurement(request.getProcessId(), request.getObject(), request.getOrganizationIdentifier(), workpack);
    }

    public void update(final ProcurementUpdateDto request) {
        ObjectUtils.updateIfPresent(request::getProcessId, this::setProcessId);
        ObjectUtils.updateIfPresent(request::getObject, this::setObject);
    }

    @Transient
    public Long getIdWorkpack() {
        return Optional.ofNullable(this.workpack).map(Entity::getId).orElse(null);
    }

    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public String getOrganizationIdentifier() { return this.organizationIdentifier; }
    public void setOrganizationIdentifier(final String organizationIdentifier) { this.organizationIdentifier = organizationIdentifier; }
    public Workpack getWorkpack() { return this.workpack; }
    public void setWorkpack(final Workpack workpack) { this.workpack = workpack; }
}
