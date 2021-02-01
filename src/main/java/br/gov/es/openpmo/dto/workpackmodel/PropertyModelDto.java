package br.gov.es.openpmo.dto.workpackmodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import br.gov.es.openpmo.model.domain.Session;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = IntegerModelDto.class, name = "IntegerModel"),
		@JsonSubTypes.Type(value = TextModelDto.class, name = "TextModel"),
		@JsonSubTypes.Type(value = DateModelDto.class, name = "DateModel"),
		@JsonSubTypes.Type(value = ToggleModelDto.class, name = "ToggleModel"),
		@JsonSubTypes.Type(value = UnitSelectionModelDto.class, name = "UnitSelectionModel"),
		@JsonSubTypes.Type(value = SelectionModelDto.class, name = "SelectionModel"),
		@JsonSubTypes.Type(value = TextAreaModelDto.class, name = "TextAreaModel"),
		@JsonSubTypes.Type(value = NumberModelDto.class, name = "NumberModel"),
		@JsonSubTypes.Type(value = CurrencyModelDto.class, name = "CurrencyModel"),
		@JsonSubTypes.Type(value = LocalitySelectionModelDto.class, name = "LocalitySelectionModel"),
		@JsonSubTypes.Type(value = OrganizationSelectionModelDto.class, name = "OrganizationSelectionModel") })
@ApiModel(subTypes = { IntegerModelDto.class, TextModelDto.class, DateModelDto.class, ToggleModelDto.class,
		UnitSelectionModelDto.class, SelectionModelDto.class, TextAreaModelDto.class, NumberModelDto.class, CurrencyModelDto.class,
		LocalitySelectionModelDto.class,
		OrganizationSelectionModelDto.class }, discriminator = "type", description = "Supertype of all PropertyModel.")
public abstract class PropertyModelDto  {

	private Long id;

	@NotNull
	private Long sortIndex;
	@NotBlank
	private String name;
	@NotBlank
	private String label;
	@NotNull
	private Session session;

	private boolean active;
	private boolean fullLine;
	private boolean required;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(Long sortIndex) {
		this.sortIndex = sortIndex;
	}

	public boolean isFullLine() {
		return fullLine;
	}

	public void setFullLine(boolean fullLine) {
		this.fullLine = fullLine;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}
