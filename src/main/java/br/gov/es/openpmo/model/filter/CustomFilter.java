package br.gov.es.openpmo.model.filter;

import br.gov.es.openpmo.dto.filter.CustomFilterDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.INCOMING;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Node
public class CustomFilter extends Entity {

  private String name;

  private CustomFilterEnum type;

  private boolean favorite;

  private String sortBy;

  private SortByDirectionEnum direction;

  @Relationship(value = "HAS", direction = INCOMING)
  private Person person;

  @Relationship(value = "HAS", direction = INCOMING)
  private Set<Rules> rules;

  @Relationship("FOR")
  private WorkpackModel workpackModel;


  public CustomFilter(
    final String name,
    final CustomFilterEnum type,
    final boolean favorite,
    final SortByDirectionEnum direction,
    final String sortBy,
    final WorkpackModel workpackModel,
    final Person person
  ) {
    this.name = name;
    this.type = type;
    this.favorite = favorite;
    this.direction = direction;
    this.sortBy = sortBy;
    this.workpackModel = workpackModel;
    this.person = person;
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
    return Optional.ofNullable(this.rules)
      .orElse(new HashSet<>());
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

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public void update(
    final CustomFilterDto request,
    final CustomFilterEnum customFilterEnum,
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
