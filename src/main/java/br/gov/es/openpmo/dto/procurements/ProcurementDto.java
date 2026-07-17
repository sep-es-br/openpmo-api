package br.gov.es.openpmo.dto.procurements;

import br.gov.es.openpmo.model.procurements.Procurement;

public class ProcurementDto {

    private Long id;
    private Long idWorkpack;
    private Long organizationId;
    private String organizationName;
    private Long year;
    private Long processId;
    private String processNumber;
    private String object;
    private String modality;
    private String status;
    private String protocol;

    public ProcurementDto() {
    }

    public ProcurementDto(final Procurement procurement) {
        this.id = procurement.getId();
        this.idWorkpack = procurement.getIdWorkpack();
        this.processNumber = procurement.getProcessNumber();
        this.object = procurement.getObject();
    }

    public static ProcurementDto of(final Procurement procurement) {
        return new ProcurementDto(procurement);
    }

    public Long getId() { return this.id; }
    public void setId(final Long id) { this.id = id; }
    public Long getIdWorkpack() { return this.idWorkpack; }
    public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
    public Long getOrganizationId() { return this.organizationId; }
    public void setOrganizationId(final Long organizationId) { this.organizationId = organizationId; }
    public String getOrganizationName() { return this.organizationName; }
    public void setOrganizationName(final String organizationName) { this.organizationName = organizationName; }
    public Long getYear() { return this.year; }
    public void setYear(final Long year) { this.year = year; }
    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getProcessNumber() { return this.processNumber; }
    public void setProcessNumber(final String processNumber) { this.processNumber = processNumber; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public String getModality() { return this.modality; }
    public void setModality(final String modality) { this.modality = modality; }
    public String getStatus() { return this.status; }
    public void setStatus(final String status) { this.status = status; }
    public String getProtocol() { return this.protocol; }
    public void setProtocol(final String protocol) { this.protocol = protocol; }
}
