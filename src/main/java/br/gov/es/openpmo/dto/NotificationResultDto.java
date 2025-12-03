package br.gov.es.openpmo.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonInclude;

@QueryResult
public class NotificationResultDto {

    private String fullName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ProjectEntryDto> projects;


    public NotificationResultDto() {}

    public NotificationResultDto(
        String fullName,
        String email,
        List<ProjectEntryDto> projects
    ) {
        this.fullName = fullName;
        this.email = email;
        this.projects = projects;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ProjectEntryDto> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectEntryDto> projects) {
        this.projects = projects;
    }


    public static class ProjectEntryDto {

        private String projectName;
        private String projectFullName;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String status;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<DeliverableEntryDto> deliverables;

        public ProjectEntryDto() {}

        public ProjectEntryDto(
            String projectName,
            String projectFullName,
            String status,
            List<DeliverableEntryDto> deliverables
        ) {
            this.projectName = projectName;
            this.projectFullName = projectFullName;
            this.status = status;
            this.deliverables = deliverables;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectFullName() {
            return projectFullName;
        }

        public void setProjectFullName(String projectFullName) {
            this.projectFullName = projectFullName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<DeliverableEntryDto> getDeliverables() {
            return deliverables;
        }

        public void setDeliverables(List<DeliverableEntryDto> deliverables) {
            this.deliverables = deliverables;
        }
    }

    // =============================================================
    // DTO INTERNO PARA ENTREGAS
    // =============================================================
    public static class DeliverableEntryDto {

        private String name;
        private String fullName;

        public DeliverableEntryDto() {}

        public DeliverableEntryDto(String name, String fullName) {
            this.name = name;
            this.fullName = fullName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }
}