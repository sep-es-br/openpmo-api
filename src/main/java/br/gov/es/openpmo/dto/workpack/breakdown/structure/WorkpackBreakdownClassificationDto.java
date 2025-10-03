package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackBreakdownClassificationDto {
  private boolean noSchedule;
  private boolean noScope;
  private boolean toCancel;
  private boolean deletedWithBaseline;
  private boolean isNew;

  // Construtor vazio – necessário para SDN/Jackson
  public WorkpackBreakdownClassificationDto() {}

  // Construtor completo
  public WorkpackBreakdownClassificationDto(
    boolean noSchedule,
    boolean noScope,
    boolean toCancel,
    boolean deletedWithBaseline,
    boolean isNew
  ) {
    this.noSchedule = noSchedule;
    this.noScope = noScope;
    this.toCancel = toCancel;
    this.deletedWithBaseline = deletedWithBaseline;
    this.isNew = isNew;
  }

  // Getters e Setters
  public boolean isNoSchedule() {
    return noSchedule;
  }
  public void setNoSchedule(boolean noSchedule) {
    this.noSchedule = noSchedule;
  }

  public boolean isNoScope() {
    return noScope;
  }
  public void setNoScope(boolean noScope) {
    this.noScope = noScope;
  }

  public boolean isToCancel() {
    return toCancel;
  }
  public void setToCancel(boolean toCancel) {
    this.toCancel = toCancel;
  }

  public boolean isDeletedWithBaseline() {
    return deletedWithBaseline;
  }
  public void setDeletedWithBaseline(boolean deletedWithBaseline) {
    this.deletedWithBaseline = deletedWithBaseline;
  }

  public boolean getIsNew() {
    return isNew;
  }
  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }
}