package br.gov.es.openpmo.dto.issue.response;

import br.gov.es.openpmo.dto.stakeholder.StakeholderCardViewDto;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Set;

public class IssueResponseDetailDto {

  private final Long id;
  private final String name;
  private final String plan;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private final LocalDate date;
  private final IssueResponseStatus status;
  private final Set<StakeholderCardViewDto> responsible;


  public IssueResponseDetailDto(
    final Long id,
    final String name,
    final String plan,
    final LocalDate date,
    final IssueResponseStatus status,
    final Set<StakeholderCardViewDto> responsible
  ) {
    this.id = id;
    this.name = name;
    this.plan = plan;
    this.date = date;
    this.status = status;
    this.responsible = responsible;
  }

  public static IssueResponseDetailDto of(final IssueResponse issueResponse) {
    return new IssueResponseDetailDto(
      issueResponse.getId(),
      issueResponse.getName(),
      issueResponse.getPlan(),
      issueResponse.getDate(),
      issueResponse.getStatus(),
      issueResponse.getResponsibleAsCardView()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getPlan() {
    return this.plan;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public IssueResponseStatus getStatus() {
    return this.status;
  }

  public Set<StakeholderCardViewDto> getResponsible() {
    return this.responsible;
  }
}
