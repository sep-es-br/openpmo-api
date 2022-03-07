package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.utils.PropertyModelType;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_INVALID_TYPE;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_NOT_FOUND;
import static java.text.MessageFormat.format;

public interface ApplyFilterUsingPropertyModel {

    default String buildFilterRuleForWorkpack(final Rules rule, final String label) {
        final String propertyName = rule.getPropertyName();
        final String operador = rule.getRelationalOperator().getOperador();

        return this.isIdentifier(propertyName)
                ? getPropertyModelFilter(propertyName, operador, label)
                : format("({0}.{1} {2} ${3}) ", this.getNodeName(), propertyName, operador, label);
    }

    String getNodeName();

    void setNodeName(String nodeName);

    default String findPropertyValue(final String propertyName) {
        final Long propertyModelId = Long.valueOf(propertyName);
        final PropertyModel propertyModel = getPropertyModel(propertyModelId);
        return propertyModel.getName();
    }

    PropertyModelRepository getPropertyModelRepository();

    default boolean isIdentifier(final String propertyName) {
        return propertyName.chars().allMatch(Character::isDigit);
    }

    default void buildOrderingAndDirectionClauseForWorkpack(final CustomFilter filter, final StringBuilder query) {
        final String field;

        if (this.isIdentifier(filter.getSortBy())) {
            this.setNodeName("propertyModel");
            field = this.findPropertyValue(filter.getSortBy());
        } else {
            field = filter.getSortBy();
        }

        if (field == null) {
            return;
        }

        query.append(" ORDER BY ")
                .append(this.getNodeName())
                .append(".")
                .append(field)
                .append(" ")
                .append(filter.getDirection());
    }

    default String getPropertyModelFilter(String propertyName, final String operador, final String label) {
        final Long propertyModelId = Long.valueOf(propertyName);
        final PropertyModel propertyModel = getPropertyModel(propertyModelId);
        final String typeName = getTypeName(propertyModel);

        if (isValueDirectComparable(typeName)) {
            return format("ANY(p IN properties WHERE p {0} ${1}) ", operador, label);
        }

        final String format = "=".equals(operador) ? "" : "NOT ";

        if (isValueInArray(typeName)) {
            return format("ANY(p IN properties WHERE NOT p IS NULL AND p<>'''' AND " +
                    "ALL(q IN ${1} WHERE {0}toLower(toString(p)) CONTAINS toLower(q))) ", format, label);
        }

        if (isValueInSelectedValue(typeName)) {
            return format("{0}${1} IN selectedValues ", format, label);
        }

        throw new NegocioException(PROPERTY_MODEL_INVALID_TYPE);
    }

    default boolean isValueInSelectedValue(String typeName) {
        return PropertyModelType.TYPE_NAME_MODEL_LOCALITY_SELECTION.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_ORGANIZATION_SELECTION.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_UNIT_SELECTION.equals(typeName);
    }

    default String getTypeName(PropertyModel propertyModel) {
        return propertyModel.getClass().getTypeName();
    }

    default boolean isValueInArray(String typeName) {
        return PropertyModelType.TYPE_NAME_MODEL_SELECTION.equals(typeName);
    }

    default boolean isValueDirectComparable(String typeName) {
        return PropertyModelType.TYPE_NAME_MODEL_CURRENCY.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_INTEGER.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_NUMBER.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_DATE.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_TEXT.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_TEXT_AREA.equals(typeName)
                || PropertyModelType.TYPE_NAME_MODEL_TOGGLE.equals(typeName);
    }

    default PropertyModel getPropertyModel(Long idProperyModel) {
        return this.getPropertyModelRepository()
                .findById(idProperyModel)
                .orElseThrow(() -> new NegocioException(PROPERTY_MODEL_NOT_FOUND));
    }

}
