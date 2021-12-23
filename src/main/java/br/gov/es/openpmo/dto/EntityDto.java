package br.gov.es.openpmo.dto;

import br.gov.es.openpmo.model.Entity;

public class EntityDto {
  private Long id;

  public EntityDto() {
  }

  public EntityDto(final Long id) {
    this.id = id;
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
}
