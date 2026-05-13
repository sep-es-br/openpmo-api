package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.office.Office;
import org.neo4j.ogm.annotation.*;


@RelationshipEntity(type = "IS_REGISTERED_IN")
public class OrganizationOfficeRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Organization organization;

    @EndNode
    private Office office;

    private String phoneNumber;

    private String contactEmail;

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Office getOffice() {
        return office;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}