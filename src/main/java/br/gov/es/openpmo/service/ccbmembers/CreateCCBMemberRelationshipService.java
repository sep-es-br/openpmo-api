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

import java.util.Optional;

@Service
public class CreateCCBMemberRelationshipService implements ICreateCCBMemberRelationshipService {

  private final IsCCBMemberRepository repository;

  private final IsInContactBookOfRepository isInContactBookOfRepository;

  private final WorkpackService workpackService;

  private final PersonService personService;

  private final OfficeService officeService;

  @Autowired
  public CreateCCBMemberRelationshipService(
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
  public void createRelationship(final CCBMemberRequest request) {
    final Person person = this.createOrUpdatePersonAndRelationships(request);
    final Workpack workpack = this.findWorkpackById(request.getIdWorkpack());

    for (final MemberAs memberAs : request.getMemberAs()) {
      this.saveCCBMember(person, workpack, memberAs);
    }
  }

  private void saveCCBMember(final Person person, final Workpack workpack, final MemberAs memberAs) {
    this.repository.save(new IsCCBMemberFor(memberAs, person, workpack), 0);
  }

  private Person createOrUpdatePersonAndRelationships(final CCBMemberRequest request) {
    final PersonDto personDto = request.getPerson();
    final Long idOffice = request.getIdOffice();

    return this.findPersonByEmail(personDto.getEmail())
        .map(person -> this.updatePersonAndRelationships(person, personDto, idOffice))
        .orElseGet(() -> this.createPersonAndRelationships(personDto, idOffice));
  }

  private Optional<Person> findPersonByEmail(final String email) {
    return this.personService.findByEmail(email);
  }

  private Person updatePersonAndRelationships(
      final Person person,
      final PersonDto personDto,
      final Long idOffice
  ) {
    person.setName(personDto.getName());
    person.setFullName(personDto.getFullName());

    final Optional<IsInContactBookOf> isInContactBookOf = this.findIsInContactBookOf(person.getId(), idOffice);

    if (isInContactBookOf.isPresent()) {
      this.updateIsInContactBookOf(isInContactBookOf.get(), personDto);
    } else {
      this.createIsInContactBookOf(personDto, idOffice, person);
    }

    return this.saveOldPerson(person);
  }

  private Optional<IsInContactBookOf> findIsInContactBookOf(
      final Long idPerson,
      final Long idOffice
  ) {
    return this.isInContactBookOfRepository
        .findIsInContactBookOfByPersonIdAndOfficeId(idPerson, idOffice);
  }

  private void updateIsInContactBookOf(
      final IsInContactBookOf isInContactBookOf,
      final PersonDto personDto
  ) {
    isInContactBookOf.setPhoneNumber(personDto.getPhoneNumber());
    isInContactBookOf.setEmail(personDto.getContactEmail());
    isInContactBookOf.setAddress(personDto.getAddress());
    this.saveIsInContactBookOf(isInContactBookOf);
  }

  private Person saveOldPerson(final Person person) {
    return this.personService.saveZeroDepth(person);
  }

  private void createIsInContactBookOf(
      final PersonDto personDto,
      final Long idOffice,
      final Person person
  ) {
    final Office office = this.findOfficeById(idOffice);
    this.saveIsInContactBookOf(new IsInContactBookOf(personDto, office, person));
  }

  private void saveIsInContactBookOf(final IsInContactBookOf isInContactBookOf) {
    this.isInContactBookOfRepository.save(isInContactBookOf, 0);
  }

  private Office findOfficeById(final Long idOffice) {
    return this.officeService.findById(idOffice);
  }

  private Person createPersonAndRelationships(
      final PersonDto personDto,
      final Long idOffice
  ) {
    return this.personService.savePerson(personDto.getEmail(), personDto, idOffice);
  }

  private Workpack findWorkpackById(final Long idWorkpack) {
    return this.workpackService.findByIdDefault(idWorkpack);
  }

}
