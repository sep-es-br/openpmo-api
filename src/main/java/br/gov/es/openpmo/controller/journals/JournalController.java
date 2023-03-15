package br.gov.es.openpmo.controller.journals;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.PageResponse;
import br.gov.es.openpmo.dto.Response;
import br.gov.es.openpmo.dto.journals.JournalRequest;
import br.gov.es.openpmo.dto.journals.JournalResponse;
import br.gov.es.openpmo.model.journals.JournalType;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.journals.JournalCreator;
import br.gov.es.openpmo.service.journals.JournalFinder;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.utils.ResponseHandler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Api
@RestController
@RequestMapping("/journal")
public class JournalController {

  private final TokenService tokenService;

  private final ResponseHandler responseHandler;

  private final JournalFinder journalFinder;

  private final JournalCreator journalCreator;

  private final ICanAccessService canAccessService;

  @Autowired
  public JournalController(
      final TokenService tokenService,
      final ResponseHandler responseHandler,
      final JournalFinder journalFinder,
      final JournalCreator journalCreator,
      final ICanAccessService canAccessService) {
    this.tokenService = tokenService;
    this.responseHandler = responseHandler;
    this.journalFinder = journalFinder;
    this.journalCreator = journalCreator;
    this.canAccessService = canAccessService;
  }

  @GetMapping
  public PageResponse<JournalResponse> getJournals(
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") final LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") final LocalDate to,
      @RequestParam(required = false) final List<Integer> scope,
      @RequestParam final List<JournalType> type,
      final UriComponentsBuilder uriComponentsBuilder,
      final Pageable pageable,
      @RequestHeader(name = "Authorization") final String authorization) {

    final Page<JournalResponse> responsePage = this.journalFinder.getAll(from, to, type, scope, uriComponentsBuilder,
        pageable);

    return PageResponse.of(responsePage);
  }

  @Transactional
  @PostMapping
  public Response<EntityDto> newInformation(
      @RequestBody final JournalRequest journalRequest,
      @RequestHeader(name = "Authorization") final String authorization) {

    this.canAccessService.ensureCanEditResource(journalRequest.getWorkpackId(), authorization);
    final Long idPerson = this.tokenService.getUserId(authorization);
    final EntityDto information = this.journalCreator.newInformation(journalRequest, idPerson);
    return this.responseHandler.success(information);
  }

}
