package br.gov.es.openpmo.service.issue;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseCreateDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseDetailDto;
import br.gov.es.openpmo.dto.issue.response.IssueResponseUpdateDto;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.issue.response.IssueResponse;
import br.gov.es.openpmo.repository.IssueResponseRepository;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.ISSUE_RESPONSE_NOT_FOUND;

@Service
public class IssueResponseService {

  private final IssueResponseRepository repository;

  private final PersonService personService;

  private final IssueService issueService;

  private final JournalCreator journalCreator;

  @Autowired
  public IssueResponseService(
      final IssueResponseRepository repository,
      final PersonService personService,
      final IssueService issueService,
      final JournalCreator journalCreator
  ) {
    this.repository = repository;
    this.personService = personService;
    this.issueService = issueService;
    this.journalCreator = journalCreator;
  }

  @Transactional
  public EntityDto create(final IssueResponseCreateDto request, final Long idPerson) {
    final Issue issue = this.findIssueById(request.getIssueId());
    final Set<Person> responsible = this.personService.findAllById(request.getResponsible());
    final IssueResponse response = IssueResponse.of(request, issue, responsible);
    this.repository.save(response);
    this.journalCreator.issueResponse(response, idPerson);
    return EntityDto.of(response);
  }

  private Issue findIssueById(final Long issueId) {
    return this.issueService.findById(issueId);
  }

  @Transactional
  public IssueResponseDetailDto update(final IssueResponseUpdateDto request, final Long idPerson) {
    final IssueResponse response = this.findById(request.getId());
    response.update(request);
    this.repository.save(response, 0);
    this.journalCreator.issueResponse(response, idPerson);
    return IssueResponseDetailDto.of(response);
  }

  private IssueResponse findById(final Long id) {
    return this.repository.findById(id)
        .orElseThrow(() -> new RegistroNaoEncontradoException(ISSUE_RESPONSE_NOT_FOUND));
  }

  @Transactional
  public void deleteById(final Long idIssueResponse) {
    this.repository.deleteById(idIssueResponse);
  }

  public IssueResponseDetailDto findByIdAsDetail(final Long id) {
    return IssueResponseDetailDto.of(this.findById(id));
  }

}
