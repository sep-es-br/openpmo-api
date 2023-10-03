package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.enumerator.GeneralOperatorsEnum;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.OfficePermissionRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class FindAllOfficePermissionByIdPersonUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final OfficePermissionRepository repository;

  @Autowired
  public FindAllOfficePermissionByIdPersonUsingCustomFilter(final OfficePermissionRepository repository) {
    this.repository = repository;
  }

  @Override
  public Session getSession() {
    return this.repository.getSession();
  }

  @Override
  public void buildMatchClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("MATCH (person:Person)-[").append(this.nodeName).append(":CAN_ACCESS_OFFICE]->")
      .append("(office:Office)").append(" ");
  }

  @Override
  public void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("WHERE ID(office)=$idOffice AND id(person)=$idPerson").append(" ");
  }

  @Override
  protected String buildCustomFilterRule(final Rules rule, final String label) {
    if (rule.getRelationalOperator() == GeneralOperatorsEnum.CONTEM) {
      switch (rule.getPropertyName()) {
        case "name":
          return MessageFormat.format("(person.name =~ ''.*'' + ${0} + ''.*'') ", label);
        case "email":
          return MessageFormat.format(
            "((auth.email =~ ''.*'' + ${0} + ''.*'') or (contact.email =~ ''.*'' + ${0} + ''.*'')) ",
            label
          );
        case "level":
        case "permissionLevel":
          return MessageFormat.format("(node.permissionLevel =~ ''.*'' + ${0} + ''.*'') ", label);
        default:
          throw new UnsupportedOperationException("Propriedade não suportada!");
      }
    } else {
      final String operador = rule.getRelationalOperator().getOperador();
      switch (rule.getPropertyName()) {
        case "name":
          return MessageFormat.format("(person.name {0} ${1}) ", operador, label);
        case "email":
          return MessageFormat.format("((auth.email {0} ${1}) or (contact.email {0} ${1}) ", operador, label);
        case "level":
        case "permissionLevel":
          return MessageFormat.format("(node.permissionLevel {0} ${1}) ", operador, label);
        default:
          throw new UnsupportedOperationException("Propriedade não suportada!");
      }
    }
  }

  @Override
  public void buildReturnClause(final StringBuilder query) {
    query.append("RETURN ").append(this.nodeName).append(", office, person");
  }

  @Override
  protected boolean hasAppendedBooleanBlock() {
    return true;
  }

  @Override
  protected boolean hasToCloseAppendedBooleanBlock() {
    return true;
  }

  @Override
  public String[] getDefinedExternalParams() {
    return new String[]{"idOffice", "idPerson"};
  }

}
