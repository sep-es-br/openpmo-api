package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.dto.organization.OrganizationDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class StakeholderDto {

  private final List<PermissionDto> permissions = new ArrayList<>(0);
  private final List<RoleDto> roles = new ArrayList<>(0);
  private Long idWorkpack;
  private PersonDto person;
  private OrganizationDto organization;

  @JsonIgnore
  private boolean isPerson;
  @JsonIgnore
  private Double score;

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public PersonDto getPerson() {
    return this.person;
  }

  public List<RoleDto> getRoles() {
    return this.roles;
  }

  public OrganizationDto getOrganization() {
    return this.organization;
  }

  public void setOrganization(final OrganizationDto organization) {
    this.isPerson = false;
    this.organization = organization;
  }

  public boolean isPerson() {
    return this.isPerson;
  }

  public void setPerson(final PersonDto person) {
    this.isPerson = true;
    this.person = person;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public Double getScore() {
    return this.score;
  }

  public void setScore(final Double score) {
    this.score = score;
  }

}
