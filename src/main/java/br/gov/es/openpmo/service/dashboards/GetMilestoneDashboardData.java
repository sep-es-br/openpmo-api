package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

@Component
public class GetMilestoneDashboardData implements IGetMilestoneDashboardData {

  private final DashboardMilestoneRepository repository;

  @Autowired
  public GetMilestoneDashboardData(final DashboardMilestoneRepository repository) {
    this.repository = repository;
  }

  @Override
  public MilestoneDataChart get(final DashboardParameters parameters) {
    final Long idBaseline = parameters.getBaselineId();
    final Long idWorkpack = parameters.getWorkpackId();
    final YearMonth yearMonth = parameters.getYearMonth();

    final LocalDate minDate = getMinOfNowAnd(yearMonth);

    final Long quantity = this.getQuantity(idBaseline, idWorkpack);
    final Long concluded = this.getConcluded(idBaseline, idWorkpack);
    final Long lateConcluded = this.getLateConcluded(idBaseline, idWorkpack);
    final Long late = this.getLate(idBaseline, idWorkpack, minDate);
    final Long onTime = this.getOnTime(idBaseline, idWorkpack, minDate);

    return new MilestoneDataChart(
        quantity,
        concluded,
        lateConcluded,
        late,
        onTime
    );
  }

  private static LocalDate getMinOfNowAnd(final YearMonth date) {
    final LocalDate now = LocalDate.now();
    final LocalDate endOfMonth = date.atEndOfMonth();
    return now.isBefore(endOfMonth) ? now : endOfMonth;
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
