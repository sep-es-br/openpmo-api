package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.repository.JournalRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;

@Service
public class EvidenceDeleter {

  private final JournalRepository journalRepository;

  private final Logger logger;

  @Value("${app.journalPath}")
  private String basePath;

  @Autowired
  public EvidenceDeleter(final JournalRepository journalRepository, final Logger logger) {
    this.journalRepository = journalRepository;
    this.logger = logger;
  }

  public void deleteEvidencesByJournalId(final Long journalId) {
    final List<File> files = this.getEvidencesByJournalId(journalId);

    for (final File file : files) {
      this.eraseEvidenceFromDisk(file);
      this.deleteEvidenceById(file);
    }
  }

  void deleteEvidenceById(final File file) {
    this.journalRepository.deleteEvidenceById(file.getId());
  }

  List<File> getEvidencesByJournalId(final Long journalId) {
    return this.journalRepository.findEvidencesByJournalId(journalId);
  }

  void eraseEvidenceFromDisk(final File fileToDelete) {
    try {
      final String filePath = this.getFilePath(fileToDelete.getUniqueNameKey());
      Files.delete(Paths.get(filePath));
    } catch (final IOException e) {
      this.logger.error("File not removed", e);
    }
  }

  String getFilePath(final String fileName) {
    return MessageFormat.format("{0}{1}", this.basePath, fileName);
  }

}
