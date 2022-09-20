package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.dto.ccbmembers.CCBMemberRequest;
import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.relations.IsCCBMemberFor;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsInContactBookOfRepository;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateCCBMemberRelationshipService implements IUpdateCCBMemberRelationshipService {

  private final IsCCBMemberRepository repository;

  private final IsInContactBookOfRepository isInContactBookOfRepository;

  private final WorkpackService workpackService;

  private final PersonService personService;

  private final OfficeService officeService;

  @Autowired
  public UpdateCCBMemberRelationshipService(
    final IsCCBMemberRepository repository,
    final IsInContactBookOfRepository isInContactBookOfRepository,
    final WorkpackService workpackService,
    final PersonService personService,
    final OfficeService officeService
  ) {
    this.repository = repository;
    this.isInContactBookOfRepository = isInContactBookOfRepository;
    this.workpackService = workpackService;
    this.personService = personService;
    this.officeService = officeService;
  }

  @Override
  public void updateRelationship(final CCBMemberRequest request) {
    final Person person = this.updatePersonAndRelationships(request);
    final Workpack workpack = this.workpackService.findByIdDefault(request.getIdWorkpack());

    this.repository.deleteAllByPersonIdAndWorkpackId(person.getId(), workpack.getId());

    for(final MemberAs memberAs : request.getMemberAs()) {
      this.repository.save(new IsCCBMemberFor(memberAs, person, workpack), 0);
    }
  }

  private Person updatePersonAndRelationships(final CCBMemberRequest request) {
    final Office office = this.officeService.findById(request.getIdOffice());
    return this.updatePerson(request.getPerson(), office);
  }

  private Person updatePerson(
    final PersonDto personDto,
    final Office office
  ) {
    final Person person = this.personService.findByIdOrElseThrow(personDto.getId());
    this.updatePersonAndRelationships(person, personDto, office);
    return person;
  }

  private void updatePersonAndRelationships(
    final Person person,
    final PersonDto personDto,
    final Office office
  ) {
    this.isInContactBookOfRepository
      .findIsInContactBookOfByPersonIdAndOfficeId(person.getId(), office.getId())
      .ifPresent(inContactBookOf -> this.updateIsInContactBookOf(inContactBookOf, personDto));
  }

  private void updateIsInContactBookOf(
    final IsInContactBookOf isInContactBookOf,
    final PersonDto personDto
  ) {
    isInContactBookOf.setPhoneNumber(personDto.getPhoneNumber());
    isInContactBookOf.setEmail(personDto.getContactEmail());
    isInContactBookOf.setAddress(personDto.getAddress());
    this.isInContactBookOfRepository.save(isInContactBookOf, 0);
  }

}
