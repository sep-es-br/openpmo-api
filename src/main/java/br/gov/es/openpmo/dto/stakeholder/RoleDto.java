package br.gov.es.openpmo.dto.stakeholder;

import java.time.LocalDate;

public class RoleDto {

    private Long id;
    private String role;
    private LocalDate from;
    private LocalDate to;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getFrom() {
        return this.from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return this.to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
