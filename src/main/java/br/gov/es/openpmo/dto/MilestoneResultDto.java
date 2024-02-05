package br.gov.es.openpmo.dto;

public class MilestoneResultDto {
    private Long concluded;
    private Long late;
    private Long lateConcluded;
    private Long onTime;
    private Long quantity;

    public MilestoneResultDto(Long concluded, Long late, Long lateConcluded, Long onTime, Long quantity) {
        this.concluded = concluded;
        this.late = late;
        this.lateConcluded = lateConcluded;
        this.onTime = onTime;
        this.quantity = quantity;
    }

    public Long getConcluded() {
        return concluded;
    }

    public void setConcluded(Long concluded) {
        this.concluded = concluded;
    }

    public Long getLate() {
        return late;
    }

    public void setLate(Long late) {
        this.late = late;
    }

    public Long getLateConcluded() {
        return lateConcluded;
    }

    public void setLateConcluded(Long lateConcluded) {
        this.lateConcluded = lateConcluded;
    }

    public Long getOnTime() {
        return onTime;
    }

    public void setOnTime(Long onTime) {
        this.onTime = onTime;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
