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

    // =============================================================
    // PROJETO
    // =============================================================
    public static class ProjectEntryDto {

        private String modelName;
        private Long id;
        private Long planId;
        private String projectName;
        private String projectFullName;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String status;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<WorkModelGroupDto> items;

        public ProjectEntryDto() {}

        public ProjectEntryDto(
            String modelName,
            String projectName,
            String projectFullName,
            String status,
            List<WorkModelGroupDto> items
        ) {
            this.modelName = modelName;
            this.projectName = projectName;
            this.projectFullName = projectFullName;
            this.status = status;
            this.items = items;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getPlanId() {
            return planId;
        }

        public void setPlanId(Long planId) {
            this.planId = planId;
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

        public List<WorkModelGroupDto> getItems() {
            return items;
        }

        public void setItems(List<WorkModelGroupDto> items) {
            this.items = items;
        }
    }

    // =============================================================
    // AGRUPADOR POR MODEL NAME (PAI)
    // =============================================================
    public static class WorkModelGroupDto {

        private String modelName;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private List<WorkEntryDto> items;

        public WorkModelGroupDto() {}

        public WorkModelGroupDto(
            String modelName,
            List<WorkEntryDto> items
        ) {
            this.modelName = modelName;
            this.items = items;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public List<WorkEntryDto> getItems() {
            return items;
        }

        public void setItems(List<WorkEntryDto> items) {
            this.items = items;
        }
    }

    // =============================================================
    // ITEM FINAL (DELIVERABLE / MILESTONE)
    // =============================================================
    public static class WorkEntryDto {

        private Long id;
        private String name;
        private String fullName;

        public WorkEntryDto() {}

        public WorkEntryDto(String name, String fullName) {
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

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
