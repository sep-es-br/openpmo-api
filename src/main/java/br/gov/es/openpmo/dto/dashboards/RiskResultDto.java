package br.gov.es.openpmo.dto.dashboards;

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
