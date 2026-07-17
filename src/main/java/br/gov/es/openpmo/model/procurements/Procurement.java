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

    private String processNumber;
    private String object;

    @Relationship(type = "RELATED_TO")
    private Workpack workpack;

    public Procurement() {
    }

    public Procurement(
        final String processNumber,
        final String object,
        final Workpack workpack
    ) {
        this.processNumber = processNumber;
        this.object = object;
        this.workpack = workpack;
    }

    public static Procurement of(
        final ProcurementCreateDto request,
        final Workpack workpack
    ) {
        return new Procurement(request.getProcessNumber(), request.getObject(), workpack);
    }

    public void update(final ProcurementUpdateDto request) {
        ObjectUtils.updateIfPresent(request::getProcessNumber, this::setProcessNumber);
        ObjectUtils.updateIfPresent(request::getObject, this::setObject);
    }

    @Transient
    public Long getIdWorkpack() {
        return Optional.ofNullable(this.workpack).map(Entity::getId).orElse(null);
    }

    public String getProcessNumber() { return this.processNumber; }
    public void setProcessNumber(final String processNumber) { this.processNumber = processNumber; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public Workpack getWorkpack() { return this.workpack; }
    public void setWorkpack(final Workpack workpack) { this.workpack = workpack; }
}
