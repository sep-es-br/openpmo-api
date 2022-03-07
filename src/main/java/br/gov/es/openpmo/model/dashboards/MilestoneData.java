package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.annotation.Transient;

@NodeEntity
public class MilestoneData extends Entity {

    private Long quantity;

    private Long concluded;

    private Long lateConcluded;

    private Long late;

    private Long onTime;

    public static MilestoneData of(MilestoneDataChart from) {
        if (from == null) {
            return null;
        }

        final MilestoneData to = new MilestoneData();

        to.setQuantity(from.getQuantity());
        to.setConcluded(from.getConcluded());
        to.setLateConcluded(from.getLateConcluded());
        to.setLate(from.getLate());
        to.setOnTime(from.getOnTime());

        return to;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setConcluded(Long concluded) {
        this.concluded = concluded;
    }

    public void setLateConcluded(Long lateConcluded) {
        this.lateConcluded = lateConcluded;
    }

    public void setLate(Long late) {
        this.late = late;
    }

    public void setOnTime(Long onTime) {
        this.onTime = onTime;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getConcluded() {
        return concluded;
    }

    public Long getLateConcluded() {
        return lateConcluded;
    }

    public Long getLate() {
        return late;
    }

    public Long getOnTime() {
        return onTime;
    }

    @Transient
    public MilestoneDataChart getResponse() {
        return new MilestoneDataChart(
                this.quantity,
                this.concluded,
                this.lateConcluded,
                this.late,
                this.onTime
        );
    }

}
