package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class ProjectIndicatorLink extends Entity {

    private IndicatorLinkCategory category;
    private String externalId;
    private String name;
    private String description;
    private String projectGoalsJson;
    private boolean active = true;

    @Relationship(type = "HAS_INDICATOR_LINK", direction = Relationship.OUTGOING)
    private Workpack workpack;

    public ProjectIndicatorLink() {
    }

    public ProjectIndicatorLink(
        final IndicatorLinkCategory category,
        final String externalId,
        final String name,
        final String description,
        final Workpack workpack
    ) {
        this.category = category;
        this.externalId = externalId;
        this.name = name;
        this.description = description;
        this.workpack = workpack;
    }

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

    public String getProjectGoalsJson() {
        return projectGoalsJson;
    }

    public void setProjectGoalsJson(final String projectGoalsJson) {
        this.projectGoalsJson = projectGoalsJson;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Workpack getWorkpack() {
        return workpack;
    }

    public void setWorkpack(final Workpack workpack) {
        this.workpack = workpack;
    }
}
