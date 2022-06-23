package br.gov.es.openpmo.repository.custom;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.LogicOperatorEnum;
import br.gov.es.openpmo.model.filter.Rules;
import org.neo4j.ogm.session.Session;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static java.text.MessageFormat.format;

public abstract class AbstractCustomFilterBuilder {

  protected String nodeName = "node";

  protected abstract Session getSession();

  protected String buildQuery(final CustomFilter filter, final Map<String, Object> params) {
    this.validateArgs(filter, params);

    final StringBuilder query = new StringBuilder();

    this.buildMatchClause(filter, query);
    this.buildWhereClause(filter, query);

    this.appendStringIfTrue(
      filter.getRules() != null && !filter.getRules().isEmpty(),
      builder -> this.buildCustomFilterWhereClause(filter, params, builder),
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
    final Map<? super String, Object> params,
    final StringBuilder builder
  ) {
    int paramCounter = 0;

    StringBuilder cache = new StringBuilder("TRUE ");

    final Iterator<Rules> iterator = filter.getRules()
      .stream()
      .sorted(Comparator.comparing(Rules::getLogicOperator))
      .iterator();

    boolean hasAppendedBooleanBlock = this.hasAppendedBooleanBlock();

    while(iterator.hasNext()) {
      final Rules rule = iterator.next();
      final String label = format("param{0}", paramCounter++);

      if(hasAppendedBooleanBlock) {
        builder.append("AND (");
        hasAppendedBooleanBlock = false;
      }

      final LogicOperatorEnum operator = rule.getLogicOperator();
      final String operand = this.buildCustomFilterRule(rule, label);

      cache = new StringBuilder(format(" {0}{1} {2} ", cache, operator, operand));
      params.put(label, this.getValue(rule));
    }

    builder.append(cache);

    if(this.hasToCloseAppendedBooleanBlock()) builder.append(")");
  }

  private String getValue(final Rules rule) {
    final String value = rule.getValue();

    if(value.contains(",")) {
      final StringBuilder stringBuilder = new StringBuilder();

      final Iterator<String> iterator = Arrays.stream(value.split(",")).iterator();

      while(iterator.hasNext()) {
        final String next = iterator.next();
        stringBuilder.append("'").append(next).append("'");

        if(iterator.hasNext()) {
          stringBuilder.append(",");
        }
      }

      return format("[{0}]", stringBuilder.toString());
    }

    return value;
  }

  protected String buildCustomFilterRule(final Rules rule, final String label) {
    return format("({0}.{1} {2} ${3}) ",
                  this.nodeName, rule.getPropertyName(), rule.getRelationalOperator().getOperador(), label
    );
  }


  protected abstract boolean hasAppendedBooleanBlock();

  protected abstract boolean hasToCloseAppendedBooleanBlock();

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

  protected void appendStringIfTrue(
    final boolean condition,
    final Consumer<? super StringBuilder> action,
    final StringBuilder builder
  ) {
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
      if(!params.containsKey(param)) {
        throw new IllegalArgumentException("Must contain '" + param + "' in parameters");
      }
    }
  }

  protected abstract String[] getDefinedExternalParams();
}
