package br.gov.es.openpmo.dto.person.detail.permissions;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

@QueryResult
public class CanAccessPlanResultDto {
    private Long idPlan;
    private Long idOffice;
    private Long idPerson;
    private PermissionLevelEnum permissionLevel;
    private String role;

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public Long getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }

    public Long getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(Long idPerson) {
        this.idPerson = idPerson;
    }

    public PermissionLevelEnum getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(PermissionLevelEnum permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
