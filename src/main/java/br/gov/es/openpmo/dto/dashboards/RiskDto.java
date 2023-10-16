package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiskDto {

  Importance importance;
  StatusOfRisk status;

  public static List<RiskDto> of(List<Risk> risks) {
    if (risks.isEmpty()) {
      return Collections.emptyList();
    }
    final List<RiskDto> riskDtos = new ArrayList<>();
    for (Risk risk : risks) {
      final RiskDto riskDto = RiskDto.of(risk);
      riskDtos.add(riskDto);
    }
    return riskDtos;
  }

  public static RiskDto of(Risk risk) {
    final RiskDto riskDto = new RiskDto();
    riskDto.setImportance(risk.getImportance());
    riskDto.setStatus(risk.getStatus());
    return riskDto;
  }

  public Importance getImportance() {
    return importance;
  }

  public void setImportance(Importance importance) {
    this.importance = importance;
  }

  public StatusOfRisk getStatus() {
    return status;
  }

  public void setStatus(StatusOfRisk status) {
    this.status = status;
  }
}
