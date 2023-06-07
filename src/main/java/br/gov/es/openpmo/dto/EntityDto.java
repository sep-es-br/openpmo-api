package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.model.Entity;
import com.fasterxml.jackson.annotation.JsonInclude;

public class EntityDto {

  private Long id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String name;

  public EntityDto() {
  }

  public EntityDto(final Long id) {
    this.id = id;
  }

  public EntityDto(
    Long id,
    String name
  ) {
    this.id = id;
    this.name = name;
  }

  public static EntityDto of(final Entity entity) {
    return new EntityDto(entity.getId());
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
