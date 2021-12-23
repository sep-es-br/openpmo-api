package br.gov.es.openpmo.controller.journals;

import br.gov.es.openpmo.dto.journals.EvidenceRequest;
import br.gov.es.openpmo.dto.journals.JournalResponse;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.service.journals.JournalFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {

  private final JournalFinder journalFinder;

  @Autowired
  public JournalController(final JournalFinder journalFinder) {
    this.journalFinder = journalFinder;
  }

  @GetMapping
  public Page<JournalResponse> getJournals(
    @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") final LocalDate from,
    @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") final LocalDate to,
    @RequestParam(required = false) final List<Integer> scope,
    @RequestParam final JournalType type,
    final UriComponentsBuilder uriComponentsBuilder,
    final Pageable pageable
  ) {
    return this.journalFinder.getAll(
      from,
      to,
      type,
      scope,
      uriComponentsBuilder,
      pageable
    );
  }

  @PostMapping
  public void newInformation(
    @RequestParam final String text,
    @RequestParam(required = false) final List<EvidenceRequest> evidences
  ) {

  }

}
