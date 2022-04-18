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

        if (date == null) {
            return now;
        }

        final LocalDate endOfMonth = date.atEndOfMonth();
        return now.isBefore(endOfMonth) ? now : endOfMonth;
    }

    @Override
    public MilestoneDataChart build(final DashboardParameters parameters) {
        final Long idBaseline = parameters.getBaselineId();
        final Long idWorkpack = parameters.getWorkpackId();
        final YearMonth yearMonth = parameters.getYearMonth();
        final LocalDate refDate = getMinOfNowAnd(yearMonth);

        Long concluded = this.getConcluded(idBaseline, idWorkpack);
        Long lateConcluded = this.getLateConcluded(idBaseline, idWorkpack);
        Long late = this.getLate(idBaseline, idWorkpack, refDate);
        Long onTime = this.getOnTime(idBaseline, idWorkpack, refDate);
        Long quantity = getQuantity(concluded, lateConcluded, late, onTime);

        return new MilestoneDataChart(
                quantity,
                concluded,
                lateConcluded,
                late,
                onTime
        );
    }

    @Override
    public MilestoneDataChart build(final Long worpackId, final YearMonth yearMonth) {
        final LocalDate refDate = getMinOfNowAnd(yearMonth);

        Long concluded = this.getConcluded(null, worpackId);
        Long lateConcluded = this.getLateConcluded(null, worpackId);
        Long late = this.getLate(null, worpackId, refDate);
        Long onTime = this.getOnTime(null, worpackId, refDate);
        Long quantity = getQuantity(concluded, lateConcluded, late, onTime);

        return new MilestoneDataChart(
                quantity,
                concluded,
                lateConcluded,
                late,
                onTime
        );
    }

    private Long getQuantity(Long concluded, Long lateConcluded, Long late, Long onTime) {
        return Optional.ofNullable(concluded).orElse(0L)
                + Optional.ofNullable(lateConcluded).orElse(0L)
                + Optional.ofNullable(late).orElse(0L)
                + Optional.ofNullable(onTime).orElse(0L);
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
        if (refDate == null) {
            return null;
        }

        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.late(id, idWorkpack, refDate))
                .orElseGet(() -> this.repository.late(idWorkpack, refDate));
    }

    private Long getOnTime(final Long baselineId, final Long idWorkpack, final LocalDate refDate) {
        if (refDate == null) {
            return null;
        }

        return Optional.ofNullable(baselineId)
                .map(id -> this.repository.onTime(id, idWorkpack, refDate))
                .orElseGet(() -> this.repository.onTime(idWorkpack, refDate));
    }

}
