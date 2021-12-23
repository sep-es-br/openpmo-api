package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.dto.journals.JournalResponse;
import br.gov.es.openpmo.model.journals.JournalEntry;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalFinder {

  private final JournalRepository journalRepository;

  private final JournalResponseMapper journalResponseMapper;

  @Autowired
  public JournalFinder(final JournalRepository journalRepository, final JournalResponseMapper journalResponseMapper) {
    this.journalRepository = journalRepository;
    this.journalResponseMapper = journalResponseMapper;
  }

  public Page<JournalResponse> getAll(
    final LocalDate from,
    final LocalDate to,
    final JournalType journalType,
    final List<Integer> scope,
    final UriComponentsBuilder uriComponentsBuilder,
    final Pageable pageable
  ) {
    final List<JournalEntry> journalEntries = this.journalRepository.findAll(
      from,
      to,
      journalType,
      scope
    );

    return getJournalEntryPage(journalEntries, pageable)
      .map(journalEntry -> this.getResponse(uriComponentsBuilder, journalEntry));
  }

  private static PageImpl<JournalEntry> getJournalEntryPage(
    final Collection<? extends JournalEntry> journalEntries,
    final Pageable pageable
  ) {
    final List<JournalEntry> pageList = journalEntries.stream()
      .skip((long) pageable.getPageSize() * pageable.getPageNumber())
      .limit(pageable.getPageSize())
      .collect(Collectors.toList());

    return new PageImpl<>(pageList, pageable, journalEntries.size());
  }

  private JournalResponse getResponse(final UriComponentsBuilder uriComponentsBuilder, final JournalEntry journalEntry) {
    return this.journalResponseMapper.map(journalEntry, uriComponentsBuilder);
  }

}
