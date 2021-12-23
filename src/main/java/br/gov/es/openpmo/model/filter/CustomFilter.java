package br.gov.es.openpmo.model.filter;

import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class CustomFilter extends Entity {

  private String name;

  private CustomFilterEnum type;

  private boolean favorite;

  private String sortBy;

  private SortByDirectionEnum direction;

  @Relationship(value = "HAS", direction = Relationship.INCOMING)
  private Set<Rules> rules;

  @Relationship("FOR")
  private WorkpackModel workpackModel;

  public CustomFilter() {
  }

  public CustomFilter(
    final String name, final CustomFilterEnum type, final boolean favorite, final SortByDirectionEnum direction,
    final String sortBy, final WorkpackModel workpackModel
  ) {
    this.name = name;
    this.type = type;
    this.favorite = favorite;
    this.direction = direction;
    this.sortBy = sortBy;
    this.workpackModel = workpackModel;
  }

  public WorkpackModel getWorkpackModel() {
    return this.workpackModel;
  }

  public void setWorkpackModel(final WorkpackModel workpackModel) {
    this.workpackModel = workpackModel;
  }

  public String getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final String sortBy) {
    this.sortBy = sortBy;
  }

  public Set<Rules> getRules() {
    return this.rules;
  }

  public void setRules(final Set<Rules> rules) {
    this.rules = rules;
  }

  public SortByDirectionEnum getDirection() {
    return this.direction;
  }

  public void setDirection(final SortByDirectionEnum direction) {
    this.direction = direction;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public boolean isFavorite() {
    return this.favorite;
  }

  public void setFavorite(final boolean favorite) {
    this.favorite = favorite;
  }

  public CustomFilterEnum getType() {
    return this.type;
  }

  public void setType(final CustomFilterEnum type) {
    this.type = type;
  }

  public void update(
    final CustomFilterDto request,
    final CustomFilterEnum customFilterEnum,
    final CustomFilter customFilter,
    final WorkpackModel workpackModel
  ) {
    this.setDirection(request.getSortByDirection());
    this.setFavorite(request.getFavorite());
    this.setName(request.getName());
    this.setSortBy(request.getSortBy());
    this.setWorkpackModel(workpackModel);
    this.setType(customFilterEnum);
  }
}
