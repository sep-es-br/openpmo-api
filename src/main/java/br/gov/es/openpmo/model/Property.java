package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Integer.class, name = "Integer"),
		@JsonSubTypes.Type(value = Text.class, name = "Text"),
		@JsonSubTypes.Type(value = Date.class, name = "Date"),
		@JsonSubTypes.Type(value = Toggle.class, name = "Toggle"),
		@JsonSubTypes.Type(value = UnitSelection.class, name = "UnitSelection"),
		@JsonSubTypes.Type(value = Selection.class, name = "Selection"),
		@JsonSubTypes.Type(value = TextArea.class, name = "TextArea"),
		@JsonSubTypes.Type(value = Number.class, name = "Number"),
		@JsonSubTypes.Type(value = Currency.class, name = "Currency"),
		@JsonSubTypes.Type(value = LocalitySelection.class, name = "LocalitySelection"),
		@JsonSubTypes.Type(value = OrganizationSelection.class, name = "OrganizationSelection") })
@ApiModel(subTypes = { Integer.class, Text.class, Date.class, Toggle.class,
		UnitSelection.class, Selection.class, TextArea.class, Number.class, Currency.class,
		LocalitySelection.class,
		OrganizationSelection.class }, discriminator = "type", description = "Supertype of all Property.")
@NodeEntity
public class Property extends Entity {

}
