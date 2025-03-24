package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.dto.journals.EvidenceField;
import br.gov.es.openpmo.dto.journals.InformationField;
import br.gov.es.openpmo.dto.journals.JournalResponse;
import br.gov.es.openpmo.dto.journals.OfficeField;
import br.gov.es.openpmo.dto.journals.PersonField;
import br.gov.es.openpmo.dto.journals.PlanField;
import br.gov.es.openpmo.dto.journals.SimpleJournalResponse;
import br.gov.es.openpmo.dto.journals.WorkpackField;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class JournalResponseMapper {

  private final WorkpackRepository workpackRepository;

  private final WorkpackModelRepository workpackModelRepository;

  private final JournalRepository journalRepository;

  @Value("${app.homeURI}")
  private String homeURI;

  @Autowired
  public JournalResponseMapper(
    final WorkpackRepository workpackRepository,
    final WorkpackModelRepository workpackModelRepository,
    final JournalRepository journalRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.journalRepository = journalRepository;
  }

  public JournalResponse map(
    final JournalEntry journalEntry,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final JournalResponse journalResponse = new JournalResponse();

    journalResponse.setType(journalEntry.getType());
    journalResponse.setAction(journalEntry.getAction());
    journalResponse.setDate(journalEntry.getDate());
    journalResponse.setLevel(journalEntry.getLevel());

    final InformationField informationField = getInformationField(journalEntry);
    journalResponse.setInformationField(informationField);

    final Set<EvidenceField> evidenceFieldSet = this.getEvidenceFieldSet(journalEntry, uriComponentsBuilder);
    journalResponse.setEvidenceFieldSet(evidenceFieldSet);

    final PersonField authorField = this.getAuthorField(journalEntry);
    journalResponse.setAuthorField(authorField);

    final PersonField targetField = this.getTargetField(journalEntry);
    journalResponse.setTargetField(targetField);

    final WorkpackField workpackField = this.getWorkpackField(journalEntry);
    journalResponse.setWorkpackField(workpackField);

    final OfficeField officeField = this.getOfficeField(journalEntry);
    journalResponse.setOfficeField(officeField);

    final PlanField planField = this.getPlanField(journalEntry);
    journalResponse.setPlanField(planField);

    return journalResponse;
  }

  public SimpleJournalResponse mapSimple(
      final JournalEntry journalEntry,
      final UriComponentsBuilder uriComponentsBuilder
  ) {
    final SimpleJournalResponse journalResponse = new SimpleJournalResponse();

    journalResponse.setDate(journalEntry.getDate());

    final InformationField informationField = getInformationField(journalEntry);
    journalResponse.setInformationField(informationField);

    final Set<EvidenceField> evidenceFieldSet = this.getEvidenceFieldSet(journalEntry, uriComponentsBuilder);
    journalResponse.setEvidenceFieldSet(evidenceFieldSet);

    final PersonField authorField = this.getAuthorField(journalEntry);
    journalResponse.setAuthorField(authorField);

    final WorkpackField workpackField = this.getWorkpackField(journalEntry);
    journalResponse.setWorkpackField(workpackField);

    return journalResponse;
  }

  private static InformationField getInformationField(final JournalEntry journalEntry) {
    final InformationField informationField = new InformationField();
    informationField.setTitle(journalEntry.getNameItem());
    informationField.setDescription(journalEntry.getDescription());
    informationField.setReason(journalEntry.getReason());
    informationField.setNewDate(journalEntry.getNewDate());
    informationField.setPreviousDate(journalEntry.getPreviousDate());
    return informationField;
  }

  private Set<EvidenceField> getEvidenceFieldSet(
    final JournalEntry journalEntry,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getFiles(journalEntry).stream()
      .map(file -> this.getEvidenceField(file, uriComponentsBuilder))
      .collect(Collectors.toSet());
  }

  private PersonField getAuthorField(final JournalEntry journalEntry) {
    final PersonField personField = new PersonField();
    final Person person = this.getAuthor(journalEntry);
    personField.setId(person.getId());
    personField.setName(person.getName());
    return personField;
  }

  private PersonField getTargetField(final JournalEntry journalEntry) {
    final Optional<Person> maybeTarget = this.maybeTarget(journalEntry);
    if (!maybeTarget.isPresent()) return null;
    final PersonField personField = new PersonField();
    final Person target = maybeTarget.get();
    personField.setId(target.getId());
    personField.setName(target.getName());
    return personField;
  }

  @Nullable
  private WorkpackField getWorkpackField(final JournalEntry journalEntry) {
    if (getTypes().contains(journalEntry.getType())) {
      return null;
    }

    final WorkpackField workpackField = new WorkpackField();

    final Long workpackId = this.getWorkpackId(journalEntry);
    workpackField.setId(workpackId);

    workpackField.setName(this.getWorkpackName(workpackId));
    workpackField.setWorkpackModelName(this.getWorkpackModelName(workpackId));
    return workpackField;
  }

  private OfficeField getOfficeField(final JournalEntry journalEntry) {
    final Optional<Office> maybeOffice = this.maybeOffice(journalEntry);
    if (!maybeOffice.isPresent()) return null;
    final OfficeField officeField = new OfficeField();
    final Office office = maybeOffice.get();
    officeField.setId(office.getId());
    officeField.setName(office.getName());
    return officeField;
  }

  private PlanField getPlanField(final JournalEntry journalEntry) {
    final Optional<Plan> maybePlan = this.maybePlan(journalEntry);
    if (!maybePlan.isPresent()) return null;
    final PlanField planField = new PlanField();
    final Plan plan = maybePlan.get();
    planField.setId(plan.getId());
    planField.setName(plan.getName());
    return planField;
  }

  private List<File> getFiles(final JournalEntry journalEntry) {
    return this.journalRepository.findEvidencesByJournalId(journalEntry.getId());
  }

  private EvidenceField getEvidenceField(
    final File file,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final EvidenceField evidenceField = new EvidenceField();
    evidenceField.setId(file.getId());
    evidenceField.setMimeType(file.getMimeType());
    evidenceField.setName(file.getUserGivenName());
    evidenceField.setUrl(this.getUrl(file, uriComponentsBuilder));
    return evidenceField;
  }

  private Person getAuthor(final JournalEntry journalEntry) {
    return this.journalRepository.findAuthorByJournalId(journalEntry.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  private Optional<Person> maybeTarget(final JournalEntry journalEntry) {
    return this.journalRepository.findTargetByJournalId(journalEntry.getId());
  }

  private static List<JournalType> getTypes() {
    final List<JournalType> types = new ArrayList<>();
    types.add(JournalType.FAIL);
    types.add(JournalType.OFFICE_PERMISSION);
    types.add(JournalType.PLAN_PERMISSION);
    return types;
  }

  private Long getWorkpackId(final JournalEntry journalEntry) {
    return this.journalRepository.findWorkpackIdByJournalId(journalEntry.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private String getWorkpackModelName(final Long workpackId) {
    return this.workpackModelRepository.findByIdWorkpack(workpackId)
      .map(WorkpackModel::getModelName)
      .orElse("");
  }

  private String getWorkpackName(final Long workpackId) {
    return this.workpackRepository.findById(workpackId).get().getName();
  }

  private Optional<Office> maybeOffice(final JournalEntry journalEntry) {
    return this.journalRepository.findOfficeByJournalId(journalEntry.getId());
  }

  private Optional<Plan> maybePlan(final JournalEntry journalEntry) {
    return this.journalRepository.findPlanByJournalId(journalEntry.getId());
  }

  private String getUrl(
    final File file,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final String homeScheme = this.homeURI.split("://")[0];
    return uriComponentsBuilder.cloneBuilder()
      .path(getEndpoint(file))
      .scheme(homeScheme)
      .build()
      .toUri()
      .toString();
  }

  private static String getEndpoint(final File file) {
    return MessageFormat.format("/evidence/image/{0,number,#}", file.getId());
  }

}
