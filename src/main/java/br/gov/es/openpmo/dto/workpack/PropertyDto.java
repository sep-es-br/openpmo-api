package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = IntegerDto.class, name = "Integer"),
		@JsonSubTypes.Type(value = TextDto.class, name = "Text"),
		@JsonSubTypes.Type(value = DateDto.class, name = "Date"),
		@JsonSubTypes.Type(value = ToggleDto.class, name = "Toggle"),
		@JsonSubTypes.Type(value = UnitSelectionDto.class, name = "UnitSelection"),
		@JsonSubTypes.Type(value = SelectionDto.class, name = "Selection"),
		@JsonSubTypes.Type(value = TextAreaDto.class, name = "TextArea"),
		@JsonSubTypes.Type(value = NumberDto.class, name = "Num"),
		@JsonSubTypes.Type(value = CurrencyDto.class, name = "Currency"),
		@JsonSubTypes.Type(value = LocalitySelectionDto.class, name = "LocalitySelection"),
		@JsonSubTypes.Type(value = OrganizationSelectionDto.class, name = "OrganizationSelection") })
@ApiModel(subTypes = { IntegerDto.class, TextDto.class, DateDto.class, ToggleDto.class,
		UnitSelectionDto.class, SelectionDto.class, TextAreaDto.class, NumberDto.class, CurrencyDto.class,
		LocalitySelectionDto.class,
		OrganizationSelectionDto.class }, discriminator = "type", description = "Supertype of all Property.")
public abstract class PropertyDto {

	private Long id;

	private Long idPropertyModel;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdPropertyModel() {
		return idPropertyModel;
	}

	public void setIdPropertyModel(Long idPropertyModel) {
		this.idPropertyModel = idPropertyModel;
	}
}
