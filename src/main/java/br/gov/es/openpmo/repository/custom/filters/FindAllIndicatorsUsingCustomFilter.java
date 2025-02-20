package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.IndicatorRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindAllIndicatorsUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

    private final IndicatorRepository indicatorRepository;

    @Autowired
    public FindAllIndicatorsUsingCustomFilter(IndicatorRepository indicatorRepository) {
        this.indicatorRepository = indicatorRepository;
    }

    @Override
    protected Session getSession() {
        return this.indicatorRepository.getSession();
    }

    @Override
    protected void buildMatchClause(
            final CustomFilter filter,
            final StringBuilder query
        ) {
        query.append("MATCH (")
            .append(this.nodeName)
            .append(":Indicator)-[isReportedFor:RELATED_TO]->(workpack:Workpack{deleted:false}) ")
            .append("WITH *, apoc.text.levenshteinSimilarity(apoc.text.clean(")
            .append(this.nodeName).append(".name), apoc.text.clean($term)) AS score ");
    }

    protected void buildWhereClause(
            final CustomFilter filter,
            final StringBuilder query
    ) {
        query.append("WHERE ").append("id(workpack)=$idWorkpack ")
            .append("AND ($term is null OR $term = '' OR score > $searchCutOffScore) ");
    }

    @Override
    protected void buildReturnClause(final StringBuilder query) {
        query.append("RETURN ").append(this.nodeName).append(", isReportedFor, workpack ");
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
    protected String[] getDefinedExternalParams() {
        return new String[] {
            "idWorkpack",
            "term",
            "searchCutOffScore"
        };
    }

}
