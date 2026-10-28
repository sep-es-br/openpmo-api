package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;
import br.gov.es.openpmo.model.indicators.ProjectIndicatorLink;
import java.math.BigDecimal;
import java.util.Map;

public class ProjectIndicatorLinkDto {

    private Long id;
    private IndicatorLinkCategory category;
    private String externalId;
    private String name;
    private String description;
    private boolean active;
    private Map<String, BigDecimal> projectGoals;

    public static ProjectIndicatorLinkDto of(
        final ProjectIndicatorLink link,
        final Map<String, BigDecimal> projectGoals
    ) {
        final ProjectIndicatorLinkDto dto = new ProjectIndicatorLinkDto();
        dto.id = link.getId();
        dto.category = link.getCategory();
        dto.externalId = link.getExternalId();
        dto.name = link.getName();
        dto.description = link.getDescription();
        dto.active = link.isActive();
        dto.projectGoals = projectGoals;
        return dto;
    }

    public Long getId() {
        return id;
    }

    public IndicatorLinkCategory getCategory() {
        return category;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public Map<String, BigDecimal> getProjectGoals() {
        return projectGoals;
    }
}
