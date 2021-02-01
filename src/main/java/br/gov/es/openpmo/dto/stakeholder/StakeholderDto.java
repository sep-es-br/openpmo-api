package br.gov.es.openpmo.dto.stakeholder;

import java.util.List;

import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.person.PersonDto;

public class StakeholderDto {

    private Long idWorkpack;
    private PersonDto person;
    private OrganizationDto organization;
    private List<RoleDto> roles;

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public OrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDto organization) {
        this.organization = organization;
    }
}
