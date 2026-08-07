package br.gov.es.openpmo.dto.obligations;

import br.gov.es.openpmo.model.obligations.Obligation;

public class ObligationDto {

    private Long id;

    private Long idWorkpack;

    private String managementUnitName;
    private String managementUnitCode;

    private Long year;

    private String obligationNumber;

    private String description;

    private String supplierCnpj;

    private String amount;

    private String protocol;

    public ObligationDto() {
    }

    public ObligationDto(final Obligation obligation) {
        this.id = obligation.getId();
        this.idWorkpack = obligation.getIdWorkpack();
        this.obligationNumber = obligation.getObligationNumber();
        this.description = obligation.getDescription();
        this.managementUnitCode = obligation.getManagementUnitCode();
    }

    public static ObligationDto of(final Obligation obligation) {
        return new ObligationDto(obligation);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getManagementUnitName() {
        return this.managementUnitName;
    }
    public String getManagementUnitCode() { return managementUnitCode; }
    public void setManagementUnitCode(String value) { managementUnitCode = value; }

    public void setManagementUnitName(
        final String managementUnitName
    ) {
        this.managementUnitName = managementUnitName;
    }

    public Long getYear() {
        return this.year;
    }

    public void setYear(final Long year) {
        this.year = year;
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

    public String getSupplierCnpj() {
        return this.supplierCnpj;
    }

    public void setSupplierCnpj(
        final String supplierCnpj
    ) {
        this.supplierCnpj = supplierCnpj;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
}
