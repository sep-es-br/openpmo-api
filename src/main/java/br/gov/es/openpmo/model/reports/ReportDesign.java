package br.gov.es.openpmo.model.reports;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class ReportDesign extends Entity {

  private String name;

  private String fullName;

  private Boolean active;

  private String query;

  private ReportFormat preferredOutputFormat;

  @Relationship("IS_DESIGNED_FOR")
  private PlanModel planModel;

  @Relationship(value = "PARAMETERIZES", direction = INCOMING)
  private Set<PropertyModel> propertiesModel;

  @Relationship(value = "IS_SOURCE_TEMPLATE_OF", direction = INCOMING)
  private Set<File> templateSource;

  @Relationship(value = "IS_COMPILED_TEMPLATE_OF", direction = INCOMING)
  private Set<File> compiledSource;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public String getQuery() {
    return this.query;
  }

  public void setQuery(final String query) {
    this.query = query;
  }

  public ReportFormat getPreferredOutputFormat() {
    return this.preferredOutputFormat;
  }

  public void setPreferredOutputFormat(final ReportFormat preferredOutputFormat) {
    this.preferredOutputFormat = preferredOutputFormat;
  }

  public PlanModel getPlanModel() {
    return this.planModel;
  }

  public void setPlanModel(final PlanModel planModel) {
    this.planModel = planModel;
  }

  public Set<PropertyModel> getPropertiesModel() {
    return this.propertiesModel;
  }

  public void setPropertiesModel(final Set<PropertyModel> propertiesModel) {
    this.propertiesModel = propertiesModel;
  }

  public Set<File> getTemplateSource() {
    return this.templateSource;
  }

  public void setTemplateSource(final Set<File> templateSource) {
    this.templateSource = templateSource;
  }

  public Set<File> getCompiledSource() {
    return this.compiledSource;
  }

  public void setCompiledSource(final Set<File> compiledSource) {
    this.compiledSource = compiledSource;
  }

  @Transient
  public Long getIdPlanModel() {
    return Optional.ofNullable(this.planModel)
      .map(Entity::getId)
      .orElse(null);
  }

  public void addAllTemplates(final Collection<File> files) {
    if (this.templateSource == null) {
      this.templateSource = new HashSet<>();
    }
    this.templateSource.addAll(files);
  }

}
