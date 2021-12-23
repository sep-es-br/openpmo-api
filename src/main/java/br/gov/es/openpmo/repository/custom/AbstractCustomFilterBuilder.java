package br.gov.es.openpmo.repository.custom;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import org.neo4j.ogm.session.Session;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractCustomFilterBuilder {

  protected String nodeName = "node";

  protected abstract Session getSession();

  protected String buildQuery(final CustomFilter filter, final Map<String, Object> params, final boolean haveExternalParam) {
    this.validateArgs(filter, params);

    final StringBuilder query = new StringBuilder();

    this.buildMatchClause(filter, query);

    this.buildWhereClause(filter, query);

    this.appendStringIfTrue(
      filter.getRules() != null && !filter.getRules().isEmpty(),
      builder -> this.buildCustomFilterWhereClause(
        filter,
        params,
        builder,
        haveExternalParam
      ),
      query
    );

    this.buildReturnClause(query);

    this.buildOrderingAndDirectionClause(filter, query);

    return query.toString();
  }

  protected abstract void buildMatchClause(CustomFilter filter, StringBuilder query);

  protected abstract void buildWhereClause(CustomFilter filter, StringBuilder query);

  protected abstract void buildReturnClause(StringBuilder query);

  protected void buildCustomFilterWhereClause(
    final CustomFilter filter,
    final Map<String, Object> params,
    final StringBuilder builder,
    final boolean haveExternalParam
  ) {
    int paramCounter = 0;
    final Iterator<Rules> iterator = filter.getRules().iterator();
    boolean firstIteration = true;

    do {
      final Rules rule = iterator.next();
      final String label = "param" + (paramCounter++);

      if(haveExternalParam && firstIteration) {
        builder.append(rule.getLogicOperator()).append(" ");
      }
      firstIteration = false;

      builder.append(this.buildCustomFilterRule(rule, label));

      params.put(label, rule.getValue());

      if(iterator.hasNext()) {
        builder.append(rule.getLogicOperator()).append(" ");
      }

    } while(iterator.hasNext());
  }

  protected String buildCustomFilterRule(final Rules rule, final String label) {
    return this.nodeName + "." + rule.getPropertyName() + " " +
           rule.getRelationalOperator().getOperador() + " " +
           "$" + label + " ";
  }

  protected void buildOrderingAndDirectionClause(final CustomFilter filter, final StringBuilder query) {
    this.appendStringIfTrue(
      filter.getSortBy() != null,
      builder -> builder.append(" ").append("ORDER BY ")
        .append(this.nodeName).append(".")
        .append(filter.getSortBy())
        .append(" ").append(filter.getDirection()),
      query
    );
  }

  protected void appendStringIfTrue(final boolean condition, final Consumer<StringBuilder> action, final StringBuilder builder) {
    if(condition) {
      action.accept(builder);
    }
  }

  protected void validateArgs(final CustomFilter filter, final Map<String, Object> params) {
    Objects.requireNonNull(params, "Parameters container must be not null");
    Objects.requireNonNull(filter, "Filter must be not null");
  }

  protected void verifyExternalParam(final Map<String, Object> params) {
    for(final String param : this.getDefinedExternalParams()) {
      final boolean notContainParam = !params.containsKey(param);
      if(notContainParam) {
        throw new IllegalArgumentException("Must contain '" + param + "' in parameters");
      }
    }
  }

  protected abstract String[] getDefinedExternalParams();
}
