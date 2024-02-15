package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.DashboardEarnedValueAnalysis;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueByStep;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.PerformanceIndexesByStep;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

public class EarnedValueAnalysisData {

  private Set<EarnedValueByStepData> earnedValueByStep;

  private Set<PerformanceIndexesData> performanceIndexes;

  public static EarnedValueAnalysisData of(final DashboardEarnedValueAnalysis from) {
    if (from == null) {
      return null;
    }

    final EarnedValueAnalysisData to = new EarnedValueAnalysisData();

    apply(from.getEarnedValueByStep(), EarnedValueByStepData::of, to::setEarnedValueByStep, HashSet::new);

    return to;
  }

  public static EarnedValueAnalysisData of(final Dashboard from, final Long baselineId) {
    if (from == null) {
      return null;
    }

    final EarnedValueAnalysisData to = new EarnedValueAnalysisData();

    apply(from.getEarnedValues(baselineId), EarnedValueByStepData::of, to::setEarnedValueByStep, HashSet::new);
    apply(from.getPerformanceIndexes(baselineId), PerformanceIndexesData::of, to::setPerformanceIndexes, HashSet::new);

    return to;
  }

  public DashboardEarnedValueAnalysis getResponse() {
    final List<EarnedValueByStep> earnedValueByStep = this.getEarnedValueByStep()
      .stream()
      .map(EarnedValueByStepData::getResponse)
      .sorted(Comparator.comparing(EarnedValueByStep::getDate))
      .collect(Collectors.toList());

    return new DashboardEarnedValueAnalysis(
      earnedValueByStep
    );
  }

  public Set<EarnedValueByStepData> getEarnedValueByStep() {
    return this.earnedValueByStep;
  }

  public void setEarnedValueByStep(final Set<EarnedValueByStepData> earnedValueByStep) {
    this.earnedValueByStep = earnedValueByStep;
  }

  public Set<PerformanceIndexesData> getPerformanceIndexes() {
    return this.performanceIndexes;
  }

  public void setPerformanceIndexes(final Set<PerformanceIndexesData> performanceIndexes) {
    this.performanceIndexes = performanceIndexes;
  }

}
