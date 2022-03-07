package br.gov.es.openpmo.repository.custom;


import br.gov.es.openpmo.model.filter.CustomFilter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class FindAllUsingCustomFilterBuilder extends AbstractCustomFilterBuilder {

    public <T> List<T> execute(final CustomFilter filter, final Map<String, Object> params) {
        this.validateArgs(filter, params);

        this.verifyExternalParam(params);

        final String query = this.buildQuery(filter, params);

        final Iterable<T> iterator = this.getSession().query(filter.getType().getNodeClass(), query, params);

        return StreamSupport.stream(iterator.spliterator(), false).collect(Collectors.toList());
    }

}
