package br.gov.es.openpmo.service.process;

import br.gov.es.openpmo.configuration.properties.AppProperties;
import br.gov.es.openpmo.dto.process.ProcessCardDto;
import br.gov.es.openpmo.dto.process.ProcessCreateDto;
import br.gov.es.openpmo.dto.process.ProcessDetailDto;
import br.gov.es.openpmo.dto.process.ProcessFromEDocsDto;
import br.gov.es.openpmo.dto.process.ProcessNumberWithIds;
import br.gov.es.openpmo.dto.process.ProcessUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ProcessRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllProcessUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.pmo.administrative_process_core.model.AdministrativeProcessDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROCESS_ID_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROCESS_NOT_FOUND;

@Service
public class ProcessService {

  private final ProcessRepository repository;

  private final AdministrativeProcessProviderService administrativeProcessProviderService;

  private final WorkpackService workpackService;

  private final FindAllProcessUsingCustomFilter findAllProcess;

  private final CustomFilterService customFilterService;

  private final AppProperties appProperties;

  @Autowired
  public ProcessService(
    final ProcessRepository repository,
    final AdministrativeProcessProviderService administrativeProcessProviderService,
    final WorkpackService workpackService,
    final FindAllProcessUsingCustomFilter findAllProcess,
    final CustomFilterService customFilterService,
    final AppProperties appProperties
  ) {
    this.repository = repository;
    this.administrativeProcessProviderService = administrativeProcessProviderService;
    this.workpackService = workpackService;
    this.findAllProcess = findAllProcess;
    this.customFilterService = customFilterService;
    this.appProperties = appProperties;
  }

  public ProcessFromEDocsDto findProcessByProtocol(
    final String protocol,
    final Long idPerson
  ) {
    final AdministrativeProcessDto process = this.administrativeProcessProviderService.getProcess(protocol, idPerson);
    return ProcessFromEDocsDto.of(process);
  }

  @Transactional
  public Process create(@Valid final ProcessCreateDto request) {
    if(Objects.isNull(request.getIdWorkpack())) {
      throw new NegocioException(ID_WORKPACK_NOT_NULL);
    }
    final Workpack workpack = this.workpackService.findById(request.getIdWorkpack());
    final Process process = Process.of(request, workpack);
    this.repository.save(process);

    return process;
  }

  @Transactional
  public ProcessDetailDto update(
    final ProcessUpdateDto request,
    final Long idPerson
  ) {
    final Process process = this.maybeFindById(request)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PROCESS_NOT_FOUND));

    final AdministrativeProcessDto processResponse = this.administrativeProcessProviderService.getProcess(
      process.getProcessNumber(),
      idPerson
    );

    final ProcessFromEDocsDto fromEDocs = ProcessFromEDocsDto.of(processResponse);

    process.update(request, fromEDocs);
    this.repository.save(process, 1);

    return ProcessDetailDto.of(processResponse, process);
  }

  private Optional<Process> maybeFindById(final ProcessUpdateDto request) {
    return this.repository.findById(request.getId());
  }

  public void deleteById(final Long id) {
    if(Objects.isNull(id)) throw new IllegalArgumentException(PROCESS_ID_NOT_NULL);

    this.repository.deleteById(id);
  }

  @Transactional
  public ProcessDetailDto findById(
    final Long idProcess,
    final Long idPerson
  ) {
    final Process process = this.repository.findById(idProcess)
      .orElseThrow(() -> new RegistroNaoEncontradoException(PROCESS_NOT_FOUND));
    final AdministrativeProcessDto processResponse = this.administrativeProcessProviderService.getProcess(
      process.getProcessNumber(),
      idPerson
    );
    
    final ProcessFromEDocsDto fromEDocs = ProcessFromEDocsDto.of(processResponse);
    this.updateProcessState(process, fromEDocs);

    return ProcessDetailDto.of(processResponse, process);
  }

  private void updateProcessState(
    final Process process,
    final ProcessFromEDocsDto fromEDocs
  ) {
    process.updateUsingEDocsData(fromEDocs);
    this.repository.save(process, 0);
  }

  public List<ProcessCardDto> findAllAsCardDto(
    final Long idWorkpack,
    final Long idFilter,
    final Long idPerson,
    final String term
  ) {

    if(idWorkpack == null) {
      throw new IllegalArgumentException(ID_WORKPACK_NOT_NULL);
    }

    if(idFilter == null) {
      return this.repository.findAllByWorkpack(idWorkpack, term, this.appProperties.getSearchCutOffScore()).stream()
        .map(ProcessCardDto::of)
        .collect(Collectors.toList());
    }

    return this.findUsingCustomFilter(
      idWorkpack,
      idFilter,
      idPerson,
      term,
      this.appProperties.getSearchCutOffScore()
    );
  }

  private List<ProcessCardDto> findUsingCustomFilter(
    final Long idWorkpack,
    final Long idFilter,
    final Long idPerson,
    final String term,
    final Double searchCutOffScore
  ) {
    final CustomFilter filter = this.customFilterService.findById(idFilter, idPerson);
    final Map<String, Object> params = new HashMap<>();

    params.put("idWorkpack", idWorkpack);
    params.put("term", term);
    params.put("searchCutOffScore", searchCutOffScore);

    final List<Process> processes = this.findAllProcess.execute(filter, params);

    return processes.stream()
      .map(ProcessCardDto::of)
      .collect(Collectors.toList());
  }

  @Transactional
  public void updateAllProcesses() {

    if (!this.administrativeProcessProviderService.isAvailable()) {
      return;
    }

    List<ProcessNumberWithIds> allProcesses = this.repository.findProcessNumbersWithIdsFromActiveWorkpacks();

    if (allProcesses.isEmpty()) {
        return;
    }

    List<String> processNumbers = allProcesses.stream()
      .map(ProcessNumberWithIds::getProcessNumber)
      .collect(Collectors.toList());
    
    List<AdministrativeProcessDto> processes = this.administrativeProcessProviderService.getProcesses(processNumbers);

    for (AdministrativeProcessDto processResponse : processes) {

      ProcessFromEDocsDto fromEDocs = ProcessFromEDocsDto.of(processResponse);

      ProcessNumberWithIds dbProcess = allProcesses.stream()
          .filter(p -> p.getProcessNumber().equals(processResponse.getProcessNumber()))
          .findFirst()
          .orElseThrow(() -> new RegistroNaoEncontradoException("Processo não encontrado no banco: " + processResponse.getProcessNumber()));

      List<Long> ids = dbProcess.getProcessIds();

      for (Long id : ids) {
          Process process = this.repository.findById(id)
              .orElseThrow(() -> new RegistroNaoEncontradoException("Processo não encontrado pelo ID: " + id));

          updateProcessState(process, fromEDocs);
      }
    }
  }  

}
