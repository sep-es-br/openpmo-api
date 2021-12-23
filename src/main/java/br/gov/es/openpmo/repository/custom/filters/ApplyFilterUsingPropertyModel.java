package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_NOT_FOUND;

public interface ApplyFilterUsingPropertyModel {

  default String buildFilterRuleForWorkpack(final Rules rule, final String label) {
    final String field;

    final String propertyName = rule.getPropertyName();

    if(this.isIdentifier(propertyName)) {
      this.setNodeName("propertyModel");
      field = this.findPropertyValue(propertyName);
    }
    else {
      field = propertyName;
    }


    return this.getNodeName() + "." + field + " " +
           rule.getRelationalOperator().getOperador() + " " +
           "$" + label + " ";
  }
  String getNodeName();

  void setNodeName(String nodeName);
  default String findPropertyValue(final String propertyName) {
    final Long propertyModelId = Long.valueOf(propertyName);

    final PropertyModel propertyModel = this.getPropertyModelRepository()
      .findById(propertyModelId)
      .orElseThrow(() -> new NegocioException(PROPERTY_MODEL_NOT_FOUND));

    return propertyModel.getName();
  }
  PropertyModelRepository getPropertyModelRepository();
  default boolean isIdentifier(final String propertyName) {
    return propertyName.chars().allMatch(Character::isDigit);
  }
  default void buildOrderingAndDirectionClauseForWorkpack(final CustomFilter filter, final StringBuilder query) {
    final String field;

    if(this.isIdentifier(filter.getSortBy())) {
      this.setNodeName("propertyModel");
      field = this.findPropertyValue(filter.getSortBy());
    }
    else {
      field = filter.getSortBy();
    }

    if(field != null) {
      query.append(" ").append("ORDER BY ")
        .append(this.getNodeName()).append(".")
        .append(field)
        .append(" ").append(filter.getDirection());

    }
  }

}
