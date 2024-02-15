package br.gov.es.openpmo.dto.dashboards;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.model.risk.Importance;

public class RiskResultDto {

    private Long high;
    private Long low;
    private Long medium;
    private Long total;

    public RiskResultDto(Long high, Long low, Long medium, Long total) {
        this.high = high;
        this.low = low;
        this.medium = medium;
        this.total = total;
    }

    public static RiskResultDto of(final List<RiskDto> riskDtos) {
        if (CollectionUtils.isNotEmpty(riskDtos)) {
            long highTotal = riskDtos.stream().filter(r -> Importance.HIGH.equals(r.getImportance())).count();
            long mediumTotal = riskDtos.stream().filter(r -> Importance.MEDIUM.equals(r.getImportance())).count();
            long lowTotal = riskDtos.stream().filter(r -> Importance.LOW.equals(r.getImportance())).count();
            long totalTotal = riskDtos.size();
            return new RiskResultDto(highTotal, lowTotal, mediumTotal, totalTotal);
        }
        return null;
    }

    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    public Long getMedium() {
        return medium;
    }

    public void setMedium(Long medium) {
        this.medium = medium;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
