package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class DashboardMilestoneService implements IDashboardMilestoneService {

    private final DashboardMilestoneRepository repository;

    @Autowired
    public DashboardMilestoneService(final DashboardMilestoneRepository repository) {
        this.repository = repository;
    }

    private static LocalDate getMinOfNowAnd(final YearMonth date) {
        final LocalDate now = LocalDate.now();
        final LocalDate endOfMonth = date.atEndOfMonth();
        return now.isBefore(endOfMonth) ? now : endOfMonth;
    }

    @Override
    public MilestoneDataChart build(final DashboardParameters parameters) {
        final Long idBaseline = parameters.getBaselineId();
        final Long idWorkpack = parameters.getWorkpackId();
        final YearMonth yearMonth = parameters.getYearMonth();
        final LocalDate minDate = getMinOfNowAnd(yearMonth);

        return new MilestoneDataChart(
                this.getQuantity(idBaseline, idWorkpack),
                this.getConcluded(idBaseline, idWorkpack),
                this.getLateConcluded(idBaseline, idWorkpack),
                this.getLate(idBaseline, idWorkpack, minDate),
                this.getOnTime(idBaseline, idWorkpack, minDate)
        );
    }

    @Override
    public MilestoneDataChart build(final Long worpackId, final YearMonth yearMonth) {
        final LocalDate minDate = getMinOfNowAnd(yearMonth);

        return new MilestoneDataChart(
                this.getQuantity(null, worpackId),
                this.getConcluded(null, worpackId),
                this.getLateConcluded(null, worpackId),
                this.getLate(null, worpackId, minDate),
                this.getOnTime(null, worpackId, minDate)
        );
    }

    private Long getQuantity(final Long baselineId, final Long idWorkpack) {
        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.quantity(id, idWorkpack))
                .orElseGet(() -> this.repository.quantity(idWorkpack));
    }

    private Long getConcluded(final Long baselineId, final Long idWorkpack) {
        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.concluded(id, idWorkpack))
                .orElseGet(() -> this.repository.concluded(idWorkpack));
    }

    private Long getLateConcluded(final Long baselineId, final Long idWorkpack) {
        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.lateConcluded(id, idWorkpack))
                .orElse(null);
    }

    private Long getLate(final Long baselineId, final Long idWorkpack, final LocalDate refDate) {
        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.late(id, idWorkpack, refDate))
                .orElseGet(() -> this.repository.late(idWorkpack, refDate));
    }

    private Long getOnTime(final Long baselineId, final Long idWorkpack, final LocalDate refDate) {
        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.onTime(id, idWorkpack, refDate))
                .orElseGet(() -> this.repository.onTime(idWorkpack, refDate));
    }

}
