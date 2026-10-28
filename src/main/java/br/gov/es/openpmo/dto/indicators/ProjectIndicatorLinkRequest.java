package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Map;

public class ProjectIndicatorLinkRequest {

    @NotNull
    private IndicatorLinkCategory category;

    @NotBlank
    @Size(max = 255)
    private String externalId;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    private Map<String, BigDecimal> projectGoals;

    public IndicatorLinkCategory getCategory() {
        return category;
    }

    public void setCategory(final IndicatorLinkCategory category) {
        this.category = category;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Map<String, BigDecimal> getProjectGoals() {
        return projectGoals;
    }

    public void setProjectGoals(final Map<String, BigDecimal> projectGoals) {
        this.projectGoals = projectGoals;
    }
}
