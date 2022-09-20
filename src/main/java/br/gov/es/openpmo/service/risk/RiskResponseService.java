package br.gov.es.openpmo.service.risk;

import br.gov.es.openpmo.dto.risk.response.RiskResponseCreateDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseDetailDto;
import br.gov.es.openpmo.dto.risk.response.RiskResponseUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.response.RiskResponse;
import br.gov.es.openpmo.repository.RiskResponseRepository;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Service;

import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.RISK_RESPONSE_ID_NULL;

@Service
public class RiskResponseService {

  private final JournalCreator journalCreator;

  private final RiskResponseRepository repository;

  private final PersonService personService;

  private final RiskService riskService;

  public RiskResponseService(
    final JournalCreator journalCreator,
    final RiskResponseRepository repository,
    final PersonService personService,
    final RiskService riskService
  ) {
    this.journalCreator = journalCreator;
    this.repository = repository;
    this.personService = personService;
    this.riskService = riskService;
  }

  public RiskResponse create(
    final RiskResponseCreateDto request,
    final Long idPerson
  ) {
    final Risk risk = this.findRiskById(request.getIdRisk());
    final Set<Person> responsible = this.findResponsible(request.getResponsible());
    final RiskResponse riskResponse = RiskResponse.of(request, risk, responsible);
    this.repository.save(riskResponse);
    this.journalCreator.riskResponse(riskResponse, idPerson);
    return riskResponse;
  }

  private Risk findRiskById(final Long id) {
    return this.riskService.findById(id);
  }

  private Set<Person> findResponsible(final Iterable<Long> responsible) {
    return this.personService.findAllById(responsible);
  }

  public void deleteById(final Long idRiskResponse) {
    if(idRiskResponse == null) throw new IllegalArgumentException(RISK_RESPONSE_ID_NULL);
    this.repository.deleteById(idRiskResponse);
  }

  public RiskResponseDetailDto update(
    final RiskResponseUpdateDto request,
    final Long idPerson
  ) {
    final RiskResponse riskResponse = this.findById(request.getId());
    this.update(request, riskResponse);
    this.repository.save(riskResponse);
    this.journalCreator.riskResponse(riskResponse, idPerson);
    return RiskResponseDetailDto.of(riskResponse);
  }

  private void update(
    final RiskResponseUpdateDto request,
    final RiskResponse riskResponse
  ) {
    final Set<Person> responsible = this.findResponsible(request.getResponsible());
    riskResponse.update(request, responsible);
  }

  private RiskResponse findById(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.RISK_RESPONSE_NOT_FOUND));
  }

  public RiskResponseDetailDto findRiskByIdAsDetailDto(final Long id) {
    return RiskResponseDetailDto.of(this.findById(id));
  }

}
