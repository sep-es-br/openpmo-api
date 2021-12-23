package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.dto.journals.EvidenceField;
import br.gov.es.openpmo.dto.journals.JournalResponse;
import br.gov.es.openpmo.dto.journals.PersonField;
import br.gov.es.openpmo.dto.journals.WorkpackField;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JournalResponseMapper {

  private final WorkpackRepository workpackRepository;

  private final JournalRepository journalRepository;

  @Autowired
  public JournalResponseMapper(final WorkpackRepository workpackRepository, final JournalRepository journalRepository) {
    this.workpackRepository = workpackRepository;
    this.journalRepository = journalRepository;
  }

  public JournalResponse map(final JournalEntry journalEntry, final UriComponentsBuilder uriComponentsBuilder) {
    final JournalResponse journalResponse = new JournalResponse();

    journalResponse.setType(journalEntry.getType());
    journalResponse.setAction(journalEntry.getAction());
    journalResponse.setDate(journalEntry.getDate());
    journalResponse.setNameItem(journalEntry.getNameItem());
    journalResponse.setDescription(journalEntry.getDescription());

    final Set<EvidenceField> evidenceFieldSet = this.getEvidenceFieldSet(journalEntry, uriComponentsBuilder);
    journalResponse.setEvidenceFieldSet(evidenceFieldSet);

    final PersonField personField = this.getPersonField(journalEntry);
    journalResponse.setPersonField(personField);

    final WorkpackField workpackField = this.getWorkpackField(journalEntry);
    journalResponse.setWorkpackField(workpackField);

    return journalResponse;
  }

  private Set<EvidenceField> getEvidenceFieldSet(
    final JournalEntry journalEntry,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    return this.getFiles(journalEntry).stream()
      .map(file -> getEvidenceField(file, uriComponentsBuilder))
      .collect(Collectors.toSet());
  }

  private List<File> getFiles(final JournalEntry journalEntry) {
    return this.journalRepository.findFileByJournalId(journalEntry.getId());
  }

  private static EvidenceField getEvidenceField(final File file, final UriComponentsBuilder uriComponentsBuilder) {
    final EvidenceField evidenceField = new EvidenceField();
    evidenceField.setId(file.getId());
    evidenceField.setMimeType(file.getMimeType());
    evidenceField.setName(file.getUserGivenName());
    evidenceField.setUrl(getUrl(file, uriComponentsBuilder));
    return evidenceField;
  }

  private static String getUrl(final File file, final UriComponentsBuilder uriComponentsBuilder) {
    return uriComponentsBuilder.cloneBuilder()
      .path(getEndpoint(file))
      .build().toUri().toString();
  }

  private static String getEndpoint(final File file) {
    return MessageFormat.format("/journal/evidence/{0}", file.getId());
  }

  private WorkpackField getWorkpackField(final JournalEntry journalEntry) {
    final WorkpackField workpackField = new WorkpackField();
    final Long workpackId = this.getWorkpackId(journalEntry);
    workpackField.setId(workpackId);
    workpackField.setName(this.getWorkpackName(workpackId));
    return workpackField;
  }

  private Long getWorkpackId(final JournalEntry journalEntry) {
    return this.journalRepository.findWorkpackIdByJournalId(journalEntry.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  @Transient
  private String getWorkpackName(final Long workpackId) {
    return this.findWorkpackNameAndFullname(workpackId)
      .map(WorkpackName::getName)
      .orElse("");
  }

  private Optional<WorkpackName> findWorkpackNameAndFullname(final Long workpackId) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpackId);
  }

  private PersonField getPersonField(final JournalEntry journalEntry) {
    final PersonField personField = new PersonField();
    final Person person = this.getPerson(journalEntry);
    personField.setId(person.getId());
    personField.setName(person.getName());
    return personField;
  }

  private Person getPerson(final JournalEntry journalEntry) {
    return this.journalRepository.findPersonByJournalId(journalEntry.getId())
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

}
