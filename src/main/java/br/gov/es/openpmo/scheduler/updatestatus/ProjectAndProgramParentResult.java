package br.gov.es.openpmo.scheduler.updatestatus;

import org.springframework.data.neo4j.annotation.QueryResult;

// TODO: verificar se será necessário utilizar
@QueryResult
public class ProjectAndProgramParentResult {
  private final Long idProgram;
  private final Long idProject;

  public ProjectAndProgramParentResult(final Long idProgram, final Long idProject) {
    this.idProgram = idProgram;
    this.idProject = idProject;
  }

  public static ProjectAndProgramParentResult EMPTY() {
    return new ProjectAndProgramParentResult(null, null);
  }

  public Long getIdProgram() {
    return this.idProgram;
  }

  public Long getIdProject() {
    return this.idProject;
  }
}
