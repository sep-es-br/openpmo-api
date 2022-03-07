package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
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

import static br.gov.es.openpmo.utils.ApplicationMessage.*;

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

    @Override
    public List<EarnedValueByStep> get(final DashboardParameters parameters) {
        final Long idProject = this.getProjectId(parameters.getWorkpackId());

        final List<EarnedValueByStep> list = new ArrayList<>();
        if (Objects.isNull(idProject)) {
            return list;
        }

        final Long idBaseline = this.getBaselineId(idProject, parameters.getBaselineId());

        final DateIntervalQuery interval = this.findIntervalInSnapshots(idBaseline);
        final YearMonth refDate = parameters.getYearMonth();

        final EarnedValueByStep accumulate = EarnedValueByStep.zeroValue();

        for (final YearMonth month : interval.toYearMonths()) {
            final EarnedValueByStepQueryResult value =
                    this.getEarnedValueByStep(idProject, idBaseline, month, refDate);

            accumulate.add(value.toEarnedValueByStep());
            list.add(accumulate.copy(false));
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
                .orElse(null);
    }

    private Long findActiveBaselineId(final Long idProject) {
        return this.baselineRepository.findActiveBaseline(idProject)
                .map(Baseline::getId)
                .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
    }

}
