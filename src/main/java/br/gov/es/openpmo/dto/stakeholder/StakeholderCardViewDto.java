package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.model.actors.Person;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Objects;

@QueryResult
public class StakeholderCardViewDto {

  private final Long id;
  private final String name;

  public StakeholderCardViewDto(
    final Long id,
    final String name
  ) {
    this.id = id;
    this.name = name;
  }

  public static StakeholderCardViewDto of(final Person person) {
    return new StakeholderCardViewDto(person.getId(), person.getName());
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final StakeholderCardViewDto that = (StakeholderCardViewDto) o;
    return this.id.equals(that.id);
  }

}
