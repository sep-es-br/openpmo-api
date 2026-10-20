package br.gov.es.openpmo.dto.universalSearch;

import java.util.List;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class UniversalSearchBreadcrumbQueryResult {
  private Long id;
  private Long programId;
  private Long principalProgramId;
  private List<UniversalSearchItemQueryResult.BreadCrumb> breadcrumbs;

  public Long getId() { return id; }
  public void setId(final Long id) { this.id = id; }
  public Long getProgramId() { return programId; }
  public void setProgramId(final Long programId) { this.programId = programId; }
  public Long getPrincipalProgramId() { return principalProgramId; }
  public void setPrincipalProgramId(final Long principalProgramId) { this.principalProgramId = principalProgramId; }
  public List<UniversalSearchItemQueryResult.BreadCrumb> getBreadcrumbs() { return breadcrumbs; }
  public void setBreadcrumbs(final List<UniversalSearchItemQueryResult.BreadCrumb> breadcrumbs) { this.breadcrumbs = breadcrumbs; }
}
