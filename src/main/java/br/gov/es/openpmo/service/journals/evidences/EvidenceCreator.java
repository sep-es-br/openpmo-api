package br.gov.es.openpmo.service.journals.evidences;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.repository.JournalRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.UUID;

@Service
public class EvidenceCreator {

  private final FileRepository fileRepository;

  private final PersonRepository personRepository;

  private final JournalRepository journalRepository;

  @Value("${app.journalPath}")
  private String basePath;

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
    this.saveFileOnDisc(file.getUniqueNameKey(), multipartFile.getBytes());
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
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  private void saveFileOnDisc(final String fileName, final byte[] data) throws IOException {
    final Path path = this.getPathFromFileName(fileName);
    final java.io.File file = getFile(path);
    try(final FileOutputStream out = new FileOutputStream(file)) {
      out.write(data);
      out.flush();
    }
  }

  private static java.io.File getFile(final Path path) throws IOException {
    return Files.createFile(path).toFile();
  }

  private Path getPathFromFileName(final String fileName) throws IOException {
    this.createPathIfDoesNotExist();
    final String filePath = this.getFilePath(fileName);
    return getPath(filePath);
  }

  private String getFilePath(final String fileName) {
    return MessageFormat.format("{0}{1}", this.basePath, fileName);
  }

  private void createPathIfDoesNotExist() throws IOException {
    final Path path = getPath(this.basePath);
    if(Files.exists(path)) {
      Files.createDirectory(path);
    }
  }

  private static Path getPath(final String filePath) {
    return Paths.get(filePath);
  }

}
