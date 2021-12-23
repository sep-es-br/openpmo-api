package br.gov.es.openpmo.repository.custom;


import br.gov.es.openpmo.model.filter.CustomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class FindAllUsingCustomFilterBuilder extends AbstractCustomFilterBuilder {

  protected Logger logger = LoggerFactory.getLogger(FindAllUsingCustomFilterBuilder.class);

  public <T> List<T> execute(final CustomFilter filter, final Map<String, Object> params) {
    this.validateArgs(filter, params);

    this.verifyExternalParam(params);

    final boolean haveExternalParam = !params.keySet().isEmpty();

    final String query = this.buildQuery(
      filter,
      params,
      haveExternalParam
    );

    this.logger.info("Custom query created successfully: '{}'\n{}", query, params);

    final Iterable<T> iterator = this.getSession().query(
      filter.getType().getNodeClass(),
      query,
      params
    );

    return StreamSupport.stream(
      iterator.spliterator(),
      false
    ).collect(Collectors.toList());
  }

}
