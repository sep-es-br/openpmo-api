package br.gov.es.openpmo.dto.journals;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

public class InformationField {

  private String title;

  private String description;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String reason;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LocalDate newDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private LocalDate previousDate;

  public String getTitle() {
    return this.title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public LocalDate getNewDate() {
    return newDate;
  }

  public void setNewDate(LocalDate newDate) {
    this.newDate = newDate;
  }

  public LocalDate getPreviousDate() {
    return previousDate;
  }

  public void setPreviousDate(LocalDate previousDate) {
    this.previousDate = previousDate;
  }

}
