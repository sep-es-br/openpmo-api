package br.gov.es.openpmo.dto.preprojects.properties;

import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaGroupValueDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.NotNull;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = PreProjectCriteriaListValueDto.class, name = "CriteriaList"),
  @JsonSubTypes.Type(value = PreProjectCriteriaSelectionValueDto.class, name = "CriteriaSelection"),
  @JsonSubTypes.Type(value = PreProjectCriteriaGroupValueDto.class, name = "CriteriaGroup")
})
public abstract class PreProjectPropertyValueDto {

  @NotNull
  private Long id;

  @NotNull
  private Long idPropertyModel;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPropertyModel() {
    return this.idPropertyModel;
  }

  public void setIdPropertyModel(final Long idPropertyModel) {
    this.idPropertyModel = idPropertyModel;
  }

}
