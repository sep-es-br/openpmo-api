package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        Long quantity = this.getQuantity(concluded, lateConcluded, late, onTime);

        return new MilestoneDataChart(
                quantity,
                concluded,
                lateConcluded,
                late,
                onTime
        );
    }

    private Long getQuantity(Long concluded, Long lateConcluded, Long late, Long onTime) {
        return Optional.ofNullable(concluded).orElse(0L) +
                Optional.ofNullable(lateConcluded).orElse(0L) +
                Optional.ofNullable(late).orElse(0L) +
                Optional.ofNullable(onTime).orElse(0L);
    }

    private Long getConcluded(final Long baselineId, final Long idWorkpack) {
        Set<Long> concluded = this.repository.concluded(idWorkpack);
        Set<Long> lateConcluded = this.repository.lateConcluded(idWorkpack);
        concluded.removeAll(lateConcluded);

        Set<Long> concludedBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.concluded(id, idWorkpack))
                .orElse(new HashSet<>());

        concluded.addAll(concludedBaseline);
        return (long) concluded.size();
    }

    public boolean isConcluded(final Long milestoneId, Long parentId, Long workpackId, Long baselineId) {
        Set<Long> concluded = this.repository.concluded(parentId);
        Set<Long> lateConcluded = this.repository.lateConcluded(baselineId, workpackId);
        concluded.removeAll(lateConcluded);

        Set<Long> concludedBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.concluded(id, workpackId))
                .orElse(new HashSet<>());

        concluded.addAll(concludedBaseline);
        return concluded.contains(milestoneId);
    }

    private Long getLateConcluded(final Long baselineId, final Long idWorkpack) {
        Set<Long> lateConcluded = Optional.ofNullable(baselineId)
                .map(id -> this.repository.lateConcluded(id, idWorkpack))
                .orElse(this.repository.lateConcluded(idWorkpack));

        return (long) lateConcluded.size();
    }

    public boolean isLateConcluded(final Long milestoneId, Long workpackId, Long baselineId) {
        Set<Long> lateConcluded = this.repository.lateConcluded(baselineId, workpackId);
        return lateConcluded.contains(milestoneId);
    }

    private Long getLate(final Long baselineId, final Long idWorkpack, final LocalDate refDate) {
        if (refDate == null) {
            return null;
        }

        Set<Long> late = this.repository.late(idWorkpack, refDate);

        Set<Long> lateBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.late(id, idWorkpack, refDate))
                .orElse(new HashSet<>());

        late.addAll(lateBaseline);
        return (long) late.size();
    }

    public boolean isLate(final Long milestoneId, Long parentId, Long workpackId, Long baselineId) {
        LocalDate refDate = LocalDate.now();

        Set<Long> late = this.repository.late(parentId, refDate);

        Set<Long> lateBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.late(id, workpackId, refDate))
                .orElse(new HashSet<>());

        late.addAll(lateBaseline);
        return late.contains(milestoneId);
    }

    private Long getOnTime(final Long baselineId, final Long idWorkpack, final LocalDate refDate) {
        if (refDate == null) {
            return null;
        }

        Set<Long> onTime = this.repository.onTime(idWorkpack, refDate);

        Set<Long> onTimeBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.onTime(id, idWorkpack, refDate))
                .orElse(new HashSet<>());

        onTime.addAll(onTimeBaseline);
        return (long) onTime.size();
    }

    public boolean isOnTime(final Long milestoneId, Long parentId, Long workpackId, Long baselineId) {
        LocalDate refDate = LocalDate.now();

        Set<Long> onTime = this.repository.onTime(parentId, refDate);

        Set<Long> onTimeBaseline = Optional.ofNullable(baselineId)
                .map(id -> this.repository.onTime(id, workpackId, refDate))
                .orElse(new HashSet<>());

        onTime.addAll(onTimeBaseline);
        return onTime.contains(milestoneId);
    }

}
