package br.gov.es.openpmo.dto.workpack;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackName {

  private final Long idWorkpackModel;
  private final Long idWorkpack;
  private final String name;
  private final String fullName;

  public WorkpackName(
    final Long idWorkpackModel,
    final Long idWorkpack,
    final String name,
    final String fullName
  ) {
    this.idWorkpackModel = idWorkpackModel;
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.fullName = fullName;
  }

  public static WorkpackName empty() {
    return new WorkpackName(
      null,
      null,
      "",
      ""
    );
  }

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

}
