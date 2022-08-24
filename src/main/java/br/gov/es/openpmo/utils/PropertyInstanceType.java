package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.dto.workpack.*;
import br.gov.es.openpmo.model.properties.Property;

import java.util.function.Function;
import java.util.function.Supplier;

public enum PropertyInstanceType {

    TYPE_MODEL_NAME_INTEGER(
            "br.gov.es.openpmo.model.properties.Integer",
            IntegerDto::new,
            IntegerDto.class,
            IntegerDto::of
    ),
    TYPE_MODEL_NAME_TEXT(
            "br.gov.es.openpmo.model.properties.Text",
            TextDto::new,
            TextDto.class,
            TextDto::of
    ),
    TYPE_MODEL_NAME_DATE(
            "br.gov.es.openpmo.model.properties.Date",
            DateDto::new,
            DateDto.class,
            DateDto::of
    ),
    TYPE_MODEL_NAME_TOGGLE(
            "br.gov.es.openpmo.model.properties.Toggle",
            ToggleDto::new,
            ToggleDto.class,
            ToggleDto::of
    ),
    TYPE_MODEL_NAME_UNIT_SELECTION(
            "br.gov.es.openpmo.model.properties.UnitSelection",
            UnitSelectionDto::new,
            UnitSelectionDto.class,
            UnitSelectionDto::of
    ),
    TYPE_MODEL_NAME_SELECTION(
            "br.gov.es.openpmo.model.properties.Selection",
            SelectionDto::new,
            SelectionDto.class,
            SelectionDto::of
    ),
    TYPE_MODEL_NAME_TEXT_AREA(
            "br.gov.es.openpmo.model.properties.TextArea",
            TextAreaDto::new,
            TextAreaDto.class,
            TextAreaDto::of
    ),
    TYPE_MODEL_NAME_NUMBER(
            "br.gov.es.openpmo.model.properties.Number",
            NumberDto::new,
            NumberDto.class,
            NumberDto::of
    ),
    TYPE_MODEL_NAME_CURRENCY(
            "br.gov.es.openpmo.model.properties.Currency",
            CurrencyDto::new,
            CurrencyDto.class,
            CurrencyDto::of
    ),
    TYPE_MODEL_NAME_LOCALITY_SELECTION(
            "br.gov.es.openpmo.model.properties.LocalitySelection",
            LocalitySelectionDto::new,
            LocalitySelectionDto.class,
            LocalitySelectionDto::of
    ),
    TYPE_MODEL_NAME_ORGANIZATION_SELECTION(
            "br.gov.es.openpmo.model.properties.OrganizationSelection",
            OrganizationSelectionDto::new,
            OrganizationSelectionDto.class,
            OrganizationSelectionDto::of
    ),
    TYPE_MODEL_NAME_GROUP(
            "br.gov.es.openpmo.model.properties.Group",
            GroupDto::new,
            GroupDto.class,
            GroupDto::of
    );

    private final String className;
    private final Supplier<? extends PropertyDto> instanceSupplier;
    private final Class<? extends PropertyDto> propertyModel;
    private final Function<Property, PropertyDto> mapper;

    PropertyInstanceType(
            final String className,
            final Supplier<? extends PropertyDto> instanceSupplier,
            final Class<? extends PropertyDto> propertyModel,
            final Function<Property, PropertyDto> mapper
    ) {
        this.className = className;
        this.instanceSupplier = instanceSupplier;
        this.propertyModel = propertyModel;
        this.mapper = mapper;
    }

    public static <R extends PropertyDto> R createFrom(final Property property) {
        for (final PropertyInstanceType type : values()) {
            if (type.isTypeOf(property)) {
                return (R) type.mapper.apply(property);
            }
        }
        throw new IllegalArgumentException(ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH);
    }

    public boolean isTypeOf(final Property model) {
        return this.getClassName().equals(model.getClass().getTypeName());
    }

    public String getClassName() {
        return this.className;
    }

    public Supplier<? extends PropertyDto> getInstanceSupplier() {
        return this.instanceSupplier;
    }

    public Class<? extends PropertyDto> getPropertyModel() {
        return this.propertyModel;
    }
}
