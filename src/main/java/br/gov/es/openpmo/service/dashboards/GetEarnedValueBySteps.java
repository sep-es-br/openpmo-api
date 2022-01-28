package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardDataParameters;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStepQueryResult;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.EarnedValueAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.INTERVAL_DATE_IN_BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class GetEarnedValueBySteps implements IGetEarnedValueBySteps {

  private final EarnedValueAnalysisRepository repository;

  private final WorkpackRepository workpackRepository;

  private final BaselineRepository baselineRepository;

  @Autowired
  public GetEarnedValueBySteps(
      final EarnedValueAnalysisRepository repository,
      final WorkpackRepository workpackRepository,
      final BaselineRepository baselineRepository
  ) {
    this.repository = repository;
    this.workpackRepository = workpackRepository;
    this.baselineRepository = baselineRepository;
  }

  private static LocalDate getReferenceDate(final DashboardDataParameters parameters) {
    final YearMonth yearMonth = Objects.requireNonNull(parameters.getYearMonth());
    return yearMonth.atEndOfMonth();
  }

  @Override
  public List<EarnedValueByStep> get(final DashboardDataParameters parameters) {
    final Long idProject = this.getProjectId(parameters.getIdWorkpack());
    final Long idBaseline = this.getBaselineId(idProject, parameters.getIdBaseline());

    final DateIntervalQuery interval = this.findIntervalInSnapshots(idBaseline);
    final YearMonth refDate = parameters.getYearMonth();

    final List<EarnedValueByStep> list = new ArrayList<>();
    final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

    for (final YearMonth month : interval.toYearMonths()) {
      final EarnedValueByStepQueryResult value =
          this.getEarnedValueByStep(idProject, idBaseline, month, refDate);

      accumulate.add(value.toEarnedValueByStep());
      list.add(accumulate.copy(showActualWorkAndEarnedValue(refDate, month)));
    }

    return list;
  }

  private Long getProjectId(final Long idWorkpack) {
    final Workpack workpack = this.findWorkpackById(idWorkpack);
    return isProject(workpack) ? idWorkpack : this.getProjectIdInParentsOf(idWorkpack);
  }

  private Long getBaselineId(final Long idProject, final Long idBaseline) {
    return Optional.ofNullable(idBaseline)
        .orElseGet(() -> this.findActiveBaselineId(idProject));
  }

  private DateIntervalQuery findIntervalInSnapshots(final Long idBaseline) {
    return this.baselineRepository.findScheduleIntervalInSnapshotsOfBaseline(idBaseline)
        .orElseThrow(() -> new NegocioException(INTERVAL_DATE_IN_BASELINE_NOT_FOUND));
  }

  private EarnedValueByStepQueryResult getEarnedValueByStep(
      final Long idProject,
      final Long idBaseline,
      final YearMonth month,
      final YearMonth referenceMonth
  ) {
    final LocalDate startOfMonth = month.atDay(1);
    final LocalDate endOfMonth = month.atEndOfMonth();
    final LocalDate referenceDate = referenceMonth.atEndOfMonth();

    return this.repository.getEarnedValueByStep(
        idProject,
        idBaseline,
        startOfMonth,
        endOfMonth,
        referenceDate
    );
  }

  private static boolean showActualWorkAndEarnedValue(final YearMonth refDate, final YearMonth month) {
    return !refDate.atEndOfMonth().isBefore(month.atEndOfMonth());
  }

  private Workpack findWorkpackById(final Long idWorkpack) {
    return this.workpackRepository.findById(idWorkpack, 0)
        .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private static boolean isProject(final Workpack workpack) {
    return workpack instanceof Project;
  }

  private Long getProjectIdInParentsOf(final Long idWorkpack) {
    return this.workpackRepository.findProjectInParentsOf(idWorkpack)
        .map(Workpack::getId)
        .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private Long findActiveBaselineId(final Long idProject) {
    return this.baselineRepository.findActiveBaselineByWorkpackId(idProject)
        .map(Baseline::getId)
        .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

}
