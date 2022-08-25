package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.*;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

import java.util.function.Function;
import java.util.function.Supplier;

public enum PropertyModelInstanceType {

  TYPE_MODEL_NAME_INTEGER(
    "br.gov.es.openpmo.model.properties.models.IntegerModel",
    IntegerModelDto::new,
    IntegerModelDto.class,
    IntegerModelDto::of
  ),
  TYPE_MODEL_NAME_TEXT(
    "br.gov.es.openpmo.model.properties.models.TextModel",
    TextModelDto::new,
    TextModelDto.class,
    TextModelDto::of
  ),
  TYPE_MODEL_NAME_DATE(
    "br.gov.es.openpmo.model.properties.models.DateModel",
    DateModelDto::new,
    DateModelDto.class,
    DateModelDto::of
  ),
  TYPE_MODEL_NAME_TOGGLE(
    "br.gov.es.openpmo.model.properties.models.ToggleModel",
    ToggleModelDto::new,
    ToggleModelDto.class,
    ToggleModelDto::of
  ),
  TYPE_MODEL_NAME_UNIT_SELECTION(
    "br.gov.es.openpmo.model.properties.models.UnitSelectionModel",
    UnitSelectionModelDto::new,
    UnitSelectionModelDto.class,
    UnitSelectionModelDto::of
  ),
  TYPE_MODEL_NAME_SELECTION(
    "br.gov.es.openpmo.model.properties.models.SelectionModel",
    SelectionModelDto::new,
    SelectionModelDto.class,
    SelectionModelDto::of
  ),
  TYPE_MODEL_NAME_TEXT_AREA(
    "br.gov.es.openpmo.model.properties.models.TextAreaModel",
    TextAreaModelDto::new,
    TextAreaModelDto.class,
    TextAreaModelDto::of
  ),
  TYPE_MODEL_NAME_NUMBER(
    "br.gov.es.openpmo.model.properties.models.NumberModel",
    NumberModelDto::new,
    NumberModelDto.class,
    NumberModelDto::of
  ),
  TYPE_MODEL_NAME_CURRENCY(
    "br.gov.es.openpmo.model.properties.models.CurrencyModel",
    CurrencyModelDto::new,
    CurrencyModelDto.class,
    CurrencyModelDto::of
  ),
  TYPE_MODEL_NAME_LOCALITY_SELECTION(
    "br.gov.es.openpmo.model.properties.models.LocalitySelectionModel",
    LocalitySelectionModelDto::new,
    LocalitySelectionModelDto.class,
    LocalitySelectionModelDto::of
  ),
  TYPE_MODEL_NAME_ORGANIZATION_SELECTION(
    "br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel",
    OrganizationSelectionModelDto::new,
    OrganizationSelectionModelDto.class,
    OrganizationSelectionModelDto::of
  ),
  TYPE_MODEL_NAME_GROUP(
    "br.gov.es.openpmo.model.properties.models.GroupModel",
    GroupModelDto::new,
    GroupModelDto.class,
    GroupModelDto::of
  );

  private final String className;
  private final Supplier<? extends PropertyModelDto> instanceSupplier;
  private final Class<? extends PropertyModelDto> propertyModel;
  private final Function<PropertyModel, PropertyModelDto> mapper;

  PropertyModelInstanceType(
    final String className,
    final Supplier<? extends PropertyModelDto> instanceSupplier,
    final Class<? extends PropertyModelDto> propertyModel,
    final Function<PropertyModel, PropertyModelDto> mapper
  ) {
    this.className = className;
    this.instanceSupplier = instanceSupplier;
    this.propertyModel = propertyModel;
    this.mapper = mapper;
  }

  public static PropertyModelDto map(final PropertyModel property) {
    for(final PropertyModelInstanceType type : values()) {
      if(type.isTypeOf(property)) {
        return type.mapper.apply(property);
      }
    }
    throw new IllegalArgumentException(ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH);
  }

  public boolean isTypeOf(final PropertyModel model) {
    return this.getClassName().equals(model.getClass().getTypeName());
  }

  public String getClassName() {
    return this.className;
  }

  public Supplier<? extends PropertyModelDto> getInstanceSupplier() {
    return this.instanceSupplier;
  }

  public Class<? extends PropertyModelDto> getPropertyModel() {
    return this.propertyModel;
  }
}
