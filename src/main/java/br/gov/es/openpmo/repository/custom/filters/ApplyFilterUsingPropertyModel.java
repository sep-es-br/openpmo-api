package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.utils.PropertyModelType;

import java.text.MessageFormat;
import java.util.Objects;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_INVALID_TYPE;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_NOT_FOUND;
import static java.text.MessageFormat.format;

public interface ApplyFilterUsingPropertyModel {

  default String buildFilterRuleForWorkpack(
    final Rules rule,
    final String label
  ) {
    final String propertyName = rule.getPropertyName();
    final String operador = rule.getRelationalOperator().getOperador();

    if (this.isIdentifier(propertyName)) {
      return this.getPropertyModelFilter(propertyName, operador, label);
    }
    final String pattern = rule.getRelationalOperator() == GeneralOperatorsEnum.CONTEM
      ? "({0}.{1} {2} ''.*'' + ${3} + ''.*'') "
      : "({0}.{1} {2} ${3}) ";
    return format(pattern, this.getNodeName(), propertyName, operador, label);
  }

  String getNodeName();

  void setNodeName(String nodeName);

  default boolean isIdentifier(final String propertyName) {
    return propertyName.chars().allMatch(Character::isDigit);
  }

  default String getPropertyModelFilter(
    final String propertyName,
    final String operador,
    final String label
  ) {
    final PropertyModel propertyModel = this.getPropertyModel(Long.valueOf(propertyName));

    final String propertyLinkedToPropertyModelTemplate = String.format(
      "(pr)-[:IS_DRIVEN_BY]->(:PropertyModel{name: '%s'})",
      propertyModel.getName()
    );

    final String typeName = this.getTypeName(propertyModel);

    if(this.isValueDirectComparable(typeName)) {
      if (Objects.equals(operador, GeneralOperatorsEnum.CONTEM.getOperador())) {
        return MessageFormat.format(
          "ANY( pr IN properties WHERE pr.value {0} ''.*'' + ${1} + ''.*'' AND {2} ) ",
          operador,
          label,
          propertyLinkedToPropertyModelTemplate
        );
      }
      return MessageFormat.format(
        "ANY( pr IN properties WHERE (pr.value {0} ${1} or toFloat(pr.value) {0} ${1}) AND {2} ) ",
        operador,
        label,
        propertyLinkedToPropertyModelTemplate
      );
    }


    if(this.isValueInArray(typeName)) {
      return MessageFormat.format(
        "ANY( " +
        "pr IN properties WHERE NOT pr.value IS NULL AND pr.value <> '''' " +
        "AND {2} " +
        "AND ALL( q IN [${1}] WHERE toLower( toString(pr.value) ) {0} toLower(q) ) " +
        " ) ",
        operador,
        label,
        propertyLinkedToPropertyModelTemplate
      );
    }

    if(this.isValueInSelectedValue(typeName)) {
      final String format = "=".equals(operador) ? "" : "NOT ";
      return MessageFormat.format("{0}${1} IN selectedValues ", format, label);
    }

    throw new NegocioException(PROPERTY_MODEL_INVALID_TYPE);
  }

  default boolean isValueInSelectedValue(final String typeName) {
    return PropertyModelType.TYPE_NAME_MODEL_LOCALITY_SELECTION.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_ORGANIZATION_SELECTION.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_UNIT_SELECTION.equals(typeName);
  }

  default String getTypeName(final PropertyModel propertyModel) {
    return propertyModel.getClass().getTypeName();
  }

  default boolean isValueInArray(final String typeName) {
    return PropertyModelType.TYPE_NAME_MODEL_SELECTION.equals(typeName);
  }

  default boolean isValueDirectComparable(final String typeName) {
    return PropertyModelType.TYPE_NAME_MODEL_CURRENCY.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_INTEGER.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_NUMBER.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_DATE.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_TEXT.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_TEXT_AREA.equals(typeName)
           || PropertyModelType.TYPE_NAME_MODEL_TOGGLE.equals(typeName);
  }

  default PropertyModel getPropertyModel(final Long idProperyModel) {
    return this.getPropertyModelRepository()
      .findById(idProperyModel)
      .orElseThrow(() -> new NegocioException(PROPERTY_MODEL_NOT_FOUND));
  }

  PropertyModelRepository getPropertyModelRepository();

  default void buildOrderingAndDirectionClauseForWorkpack(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    final String field;

    if(this.isIdentifier(filter.getSortBy())) {
      this.setNodeName("propertyModel");
      field = this.findPropertyValue(filter.getSortBy());
    }
    else {
      field = filter.getSortBy();
    }

    if(field == null) {
      return;
    }

    query.append(" ORDER BY ")
      .append(this.getNodeName())
      .append(".")
      .append(field)
      .append(" ")
      .append(filter.getDirection());
  }

  default String findPropertyValue(final String propertyName) {
    final Long propertyModelId = Long.valueOf(propertyName);
    final PropertyModel propertyModel = this.getPropertyModel(propertyModelId);
    return propertyModel.getName();
  }

}
