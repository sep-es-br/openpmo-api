package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;

public abstract class FindAllBaselineUsingCustomFilter extends FindAllUsingCustomFilterBuilder {

  private final BaselineRepository baselineRepository;

  public FindAllBaselineUsingCustomFilter(final BaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  protected Session getSession() {
    return this.baselineRepository.getSession();
  }

  @Override
  protected void buildMatchClause(
          final CustomFilter filter,
          final StringBuilder query
  ) {
    query.append("match (w:Workpack)-[ii:IS_BASELINED_BY]->(")
            .append(this.nodeName)
            .append(":Baseline), (p:Person)-[c:IS_CCB_MEMBER_FOR{active:true}]->(w), ")
            .append("(w)-[iib:IS_INSTANCE_BY]->(model:WorkpackModel), ")
            .append("(model)<-[f1:FEATURES]-(nameModel:PropertyModel{name:'name'})<-[idb:IS_DRIVEN_BY]-(nameProperty:Property)-[f2:FEATURES]->(w) ")
            .append("with *, apoc.text.levenshteinSimilarity(apoc.text.clean(").append(this.nodeName).append(".name), apoc.text.clean($term)) as nameScore, ")
            .append("apoc.text.levenshteinSimilarity(apoc.text.clean(").append(this.nodeName).append(".description), apoc.text.clean($term)) as descriptionScore ")
            .append("with *, case when nameScore > descriptionScore then nameScore else descriptionScore end as score ");
  }

  @Override
  protected void buildWhereClause(
    final CustomFilter filter,
    final StringBuilder query
  ) {
    query.append("where id(p)=$idPerson ");
    switch (this.getStatus()) {
      case WAITING_MY_EVALUATION:
        query.append("and ")
          .append(this.nodeName)
          .append(".status='PROPOSED' and not (")
          .append(this.nodeName)
          .append(")-[:IS_EVALUATED_BY]->(p) ");
        break;
      case WAITING_OTHERS_EVALUATIONS:
        query.append("and ")
          .append(this.nodeName)
          .append(".status='PROPOSED' and (")
          .append(this.nodeName)
          .append(")-[:IS_EVALUATED_BY]->(p) ");
        break;
      case APPROVEDS:
        query.append("and ").append(this.nodeName).append(".status='APPROVED' ");
        break;
      case REJECTEDS:
        query.append("and ").append(this.nodeName).append(".status='REJECTED' ");
        break;
    }
    query.append("and ($term is null or $term = '' or score > $searchCutOffScore) ");
  }

  @Override
  protected void buildReturnClause(final StringBuilder query) {
    query.append("return ").append(this.nodeName).append(", w, ii, p, iib, model, f1, f2, nameModel, idb ");
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
    return new String[]{
      "idPerson",
      "term",
      "searchCutOffScore"
    };
  }

  protected abstract BaselineViewStatus getStatus();

}
