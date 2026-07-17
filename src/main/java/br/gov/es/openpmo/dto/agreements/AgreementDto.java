package br.gov.es.openpmo.dto.agreements;

import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.model.agreements.AgreementType;

public class AgreementDto {

    private Long id;
    private Long idWorkpack;
    private AgreementType type;
    private Long organizationId;
    private String organizationName;
    private Long managementUnitId;
    private String managementUnitName;
    private Long year;
    private Long processId;
    private String processNumber;
    private String object;
    private String supplierCnpj;
    private String supplierName;
    private String grantorCnpj;
    private String grantorName;
    private String protocol;

    public AgreementDto() {
    }

    public AgreementDto(final Agreement agreement) {
        this.id = agreement.getId();
        this.idWorkpack = agreement.getIdWorkpack();
        this.type = agreement.getType();
        this.processNumber = agreement.getProcessNumber();
        this.object = agreement.getObject();
    }

    public static AgreementDto of(final Agreement agreement) { return new AgreementDto(agreement); }
    public Long getId() { return this.id; }
    public void setId(final Long id) { this.id = id; }
    public Long getIdWorkpack() { return this.idWorkpack; }
    public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
    public AgreementType getType() { return this.type; }
    public void setType(final AgreementType type) { this.type = type; }
    public Long getOrganizationId() { return this.organizationId; }
    public void setOrganizationId(final Long organizationId) { this.organizationId = organizationId; }
    public String getOrganizationName() { return this.organizationName; }
    public void setOrganizationName(final String organizationName) { this.organizationName = organizationName; }
    public Long getManagementUnitId() { return this.managementUnitId; }
    public void setManagementUnitId(final Long managementUnitId) { this.managementUnitId = managementUnitId; }
    public String getManagementUnitName() { return this.managementUnitName; }
    public void setManagementUnitName(final String managementUnitName) { this.managementUnitName = managementUnitName; }
    public Long getYear() { return this.year; }
    public void setYear(final Long year) { this.year = year; }
    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getProcessNumber() { return this.processNumber; }
    public void setProcessNumber(final String processNumber) { this.processNumber = processNumber; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public String getSupplierCnpj() { return this.supplierCnpj; }
    public void setSupplierCnpj(final String supplierCnpj) { this.supplierCnpj = supplierCnpj; }
    public String getSupplierName() { return this.supplierName; }
    public void setSupplierName(final String supplierName) { this.supplierName = supplierName; }
    public String getGrantorCnpj() { return this.grantorCnpj; }
    public void setGrantorCnpj(final String grantorCnpj) { this.grantorCnpj = grantorCnpj; }
    public String getGrantorName() { return this.grantorName; }
    public void setGrantorName(final String grantorName) { this.grantorName = grantorName; }
    public String getProtocol() { return this.protocol; }
    public void setProtocol(final String protocol) { this.protocol = protocol; }
}
