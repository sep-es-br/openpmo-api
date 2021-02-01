package br.gov.es.openpmo.model;

import java.util.Objects;

import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import br.gov.es.openpmo.model.domain.Session;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = IntegerModel.class, name = "IntegerModel"),
		@JsonSubTypes.Type(value = TextModel.class, name = "TextModel"),
		@JsonSubTypes.Type(value = DateModel.class, name = "DateModel"),
		@JsonSubTypes.Type(value = ToggleModel.class, name = "ToggleModel"),
		@JsonSubTypes.Type(value = UnitSelectionModel.class, name = "UnitSelectionModel"),
		@JsonSubTypes.Type(value = SelectionModel.class, name = "SelectionModel"),
		@JsonSubTypes.Type(value = TextAreaModel.class, name = "TextAreaModel"),
		@JsonSubTypes.Type(value = NumberModel.class, name = "NumberModel"),
		@JsonSubTypes.Type(value = CurrencyModel.class, name = "CurrencyModel"),
		@JsonSubTypes.Type(value = LocalitySelectionModel.class, name = "LocalitySelectionModel"),
		@JsonSubTypes.Type(value = OrganizationSelectionModel.class, name = "OrganizationSelectionModel") })
@ApiModel(subTypes = { IntegerModel.class, TextModel.class, DateModel.class, ToggleModel.class,
		UnitSelectionModel.class, SelectionModel.class, TextAreaModel.class, NumberModel.class, CurrencyModel.class,
		LocalitySelectionModel.class,
		OrganizationSelectionModel.class }, discriminator = "type", description = "Supertype of all PropertyModel.")
@NodeEntity
public class PropertyModel extends Entity {

	private Long sortIndex;

	private String name;
	private String label;
	private Session session;

	private boolean active;
	private boolean fullLine;
	private boolean required;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		PropertyModel that = (PropertyModel) o;
		return active == that.active && fullLine == that.fullLine && required == that.required && Objects.equals(
			sortIndex, that.sortIndex) && Objects.equals(name, that.name) && Objects.equals(label, that.label)
			&& Objects.equals(session, that.session);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), sortIndex, name, label, session, active, fullLine, required);
	}
}
