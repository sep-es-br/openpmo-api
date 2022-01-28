package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalDeleter {

  private final JournalRepository journalRepository;

  private final EvidenceDeleter evidenceDeleter;

  @Autowired
  public JournalDeleter(final JournalRepository journalRepository, final EvidenceDeleter evidenceDeleter) {
    this.journalRepository = journalRepository;
    this.evidenceDeleter = evidenceDeleter;
  }

  public void deleteJournalsByWorkpackId(final Long workpackId) {
    final List<Long> journalEntries = this.getJournalIdsByWorkpackId(workpackId);

    for (final Long journalEntry : journalEntries) {
      this.evidenceDeleter.deleteEvidencesByJournalId(journalEntry);
      this.deleteJournalById(journalEntry);
    }
  }

  private List<Long> getJournalIdsByWorkpackId(final Long workpackId) {
    return this.journalRepository.findAllJournalIdsByWorkpackId(workpackId);
  }

  private void deleteJournalById(final Long journalEntry) {
    this.journalRepository.deleteById(journalEntry);
  }

}
