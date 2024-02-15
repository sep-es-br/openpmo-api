package br.gov.es.openpmo.dto;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import br.gov.es.openpmo.dto.dashboards.MilestoneDto;

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

    public static MilestoneResultDto of(final List<MilestoneDto> milestoneDtos) {
        if (CollectionUtils.isNotEmpty(milestoneDtos)) {

            long concluded = milestoneDtos.stream().filter(m -> Boolean.TRUE.equals(m.isCompleted())
                && (m.getSnapshotDate() == null ||
                (m.getSnapshotDate().isAfter(m.getMilestoneDate()) || m.getSnapshotDate().isEqual(m.getMilestoneDate())))).count();

            long lateConcluded = milestoneDtos.stream().filter(m -> Boolean.TRUE.equals(m.isCompleted())
                && m.getSnapshotDate() != null && m.getSnapshotDate().isBefore(m.getMilestoneDate())).count();

            long late = milestoneDtos.stream().filter(m -> Boolean.FALSE.equals(m.isCompleted())
                && m.getMilestoneDate() != null && LocalDate.now().isAfter(m.getMilestoneDate())).count();

            long onTime = milestoneDtos.stream().filter(m -> Boolean.FALSE.equals(m.isCompleted())
                && m.getMilestoneDate() != null && (LocalDate.now().isBefore(m.getMilestoneDate())
                || LocalDate.now().isEqual(m.getMilestoneDate()))).count();

            long total = milestoneDtos.size();
            return new MilestoneResultDto(concluded, late, lateConcluded, onTime, total);

        }
        return null;
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
