package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.UUID;

@Service
public class EvidenceCreator {

  private final FileRepository fileRepository;

  private final PersonRepository personRepository;

  private final JournalRepository journalRepository;

  @Value("${app.journalPath}")
  private String journalPath;

  @Autowired
  public EvidenceCreator(
      final FileRepository fileRepository,
      final PersonRepository personRepository,
      final JournalRepository journalRepository
  ) {
    this.fileRepository = fileRepository;
    this.personRepository = personRepository;
    this.journalRepository = journalRepository;
  }

  public void create(final Long idJournal, final Long idPerson, final MultipartFile multipartFile) throws IOException {
    final JournalEntry journalEntry = this.findJournalById(idJournal);
    final Person person = this.findPersonById(idPerson);
    final File file = createFile(journalEntry, person, multipartFile);
    FileUtils.storeFile(this.journalPath, file.getUniqueNameKey(), multipartFile.getBytes());
    this.saveFile(file);
  }

  private void saveFile(final File file) {
    this.fileRepository.save(file);
  }

  private static File createFile(final JournalEntry journalEntry, final Person person, final MultipartFile multipartFile) {
    final File file = new File();
    file.setJournalEntry(journalEntry);
    file.setPerson(person);
    file.setUniqueNameKey(getUniqueNameKey(multipartFile));
    file.setUserGivenName(multipartFile.getOriginalFilename());
    file.setMimeType(multipartFile.getContentType());
    return file;
  }

  private static String getUniqueNameKey(final MultipartFile multipartFile) {
    final UUID randomNumber = UUID.randomUUID();
    final String originalFilename = multipartFile.getOriginalFilename();
    return MessageFormat.format("{0}{1}", randomNumber, originalFilename);
  }

  private Person findPersonById(final Long idPerson) {
    return this.personRepository.findById(idPerson)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  private JournalEntry findJournalById(final Long idJournal) {
    return this.journalRepository.findById(idJournal)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.JOURNAL_NOT_FOUND));
  }

}
