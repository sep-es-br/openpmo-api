package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.enumerator.MilestoneStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class MilestoneDetailDto extends WorkpackDetailDto {

    private MilestoneStatus milestoneStatus;

    private LocalDate milestoneDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

    private boolean isWithinAWeek;

    public MilestoneStatus getMilestoneStatus() {
        return this.milestoneStatus;
    }

    public void setMilestoneStatus(final MilestoneStatus milestoneStatus) {
        this.milestoneStatus = milestoneStatus;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(final LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isWithinAWeek() {
        return this.isWithinAWeek;
    }

    public void setWithinAWeek(final boolean withinAWeek) {
        this.isWithinAWeek = withinAWeek;
    }

    public LocalDate getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(LocalDate milestoneDate) {
        this.milestoneDate = milestoneDate;
    }
}
