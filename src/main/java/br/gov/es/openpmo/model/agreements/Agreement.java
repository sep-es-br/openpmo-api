package br.gov.es.openpmo.model.agreements;

import br.gov.es.openpmo.dto.agreements.AgreementCreateDto;
import br.gov.es.openpmo.dto.agreements.AgreementUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

@NodeEntity
public abstract class Agreement extends Entity {

    private Long processId;
    private String object;

    @Relationship(type = "SIGNED_FOR")
    private Workpack workpack;

    protected Agreement() {
    }

    protected Agreement(
        final Long processId,
        final String object,
        final Workpack workpack
    ) {
        this.processId = processId;
        this.object = object;
        this.workpack = workpack;
    }

    public static Agreement of(final AgreementCreateDto request, final Workpack workpack) {
        switch (request.getType()) {
            case CONTRACT:
                return new Contract(request.getProcessId(), request.getObject(), workpack);
            case COOPERATION:
                return new Cooperation(request.getProcessId(), request.getObject(), workpack);
            default:
                throw new IllegalArgumentException("agreement.type.invalid");
        }
    }

    public void update(final AgreementUpdateDto request) {
        if (this.getType() != request.getType()) {
            throw new IllegalArgumentException("agreement.type.cannot.change");
        }
        ObjectUtils.updateIfPresent(request::getProcessId, this::setProcessId);
        ObjectUtils.updateIfPresent(request::getObject, this::setObject);
    }

    public abstract AgreementType getType();

    @Transient
    public Long getIdWorkpack() {
        return Optional.ofNullable(this.workpack).map(Entity::getId).orElse(null);
    }

    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public Workpack getWorkpack() { return this.workpack; }
    public void setWorkpack(final Workpack workpack) { this.workpack = workpack; }
}
