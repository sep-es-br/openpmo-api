package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.GetAllBaselinesResponse;
import br.gov.es.openpmo.dto.baselines.GetAllCCBMemberBaselineResponse;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllBaselinesService implements IGetAllBaselinesService {

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public GetAllBaselinesService(
    final BaselineRepository baselineRepository,
    final WorkpackRepository workpackRepository,
    final IsCCBMemberRepository ccbMemberRepository
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.ccbMemberRepository = ccbMemberRepository;
  }

  @Override
  public List<GetAllBaselinesResponse> getAllByWorkpackId(final Long idWorkpack) {
    return this.baselineRepository.findAllByWorkpackId(idWorkpack).stream()
      .map(GetAllBaselinesService::getBaselinesResponse)
      .collect(Collectors.toList());
  }

  private static GetAllBaselinesResponse getBaselinesResponse(final Baseline baseline) {
    return new GetAllBaselinesResponse(
      baseline.getId(),
      baseline.getIdWorkpack(),
      baseline.getName(),
      baseline.getStatus(),
      baseline.getDescription(),
      baseline.getActivationDate(),
      baseline.getProposalDate(),
      baseline.getMessage(),
      baseline.isCancelation(),
      baseline.isActive()
    );
  }

  @Override
  public List<GetAllCCBMemberBaselineResponse> getAllByPersonId(final Long idPerson) {
    return this.getWorkpacks(idPerson).stream()
      .map(this::getGetAllCCBMemberBaselineResponse)
      .collect(Collectors.toList());
  }

  private GetAllCCBMemberBaselineResponse getGetAllCCBMemberBaselineResponse(final Workpack workpack) {
    final List<GetAllBaselinesResponse> baselines = new ArrayList<>();

    for(final Baseline baseline : this.getBaselines(workpack)) {
      baselines.add(getBaselinesResponse(baseline));
    }

    return new GetAllCCBMemberBaselineResponse(
      workpack.getId(),
      this.getWorkpackName(workpack),
      baselines
    );
  }

  private String getWorkpackName(final Workpack workpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName)
      .orElse(null);
  }

  private List<Baseline> getBaselines(final Workpack workpack) {
    return this.baselineRepository.findAllByWorkpackId(workpack.getId());
  }

  private List<Workpack> getWorkpacks(final Long idPerson) {
    return this.ccbMemberRepository.findAllWorkpacksByPersonId(idPerson);
  }

}
