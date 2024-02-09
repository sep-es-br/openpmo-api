package br.gov.es.openpmo.dto.baselines;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.enumerator.BaselineStatus;

@QueryResult
public class BaselineWorkpackDto {
    private Long id;
    private String name;
    private String fullName;
    private LocalDateTime date;
    private String fontIcon;
    private List<String> label;
    private Long idMaster;

    private List<BaselineScheduleStep> schedule = new ArrayList<>(0);
    private List<BaselineConsumesStep> consumes = new ArrayList<>(0);
    private BaselineStatus classification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public List<String> getLabel() {
        return label;
    }

    public void setLabel(List<String> label) {
        this.label = label;
    }

    public String getType() {
        if (label == null || label.isEmpty()) return null;
        return label.get(label.size() -1);
    }

    public List<BaselineScheduleStep> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<BaselineScheduleStep> schedule) {
        this.schedule = schedule;
    }

    public List<BaselineConsumesStep> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<BaselineConsumesStep> consumes) {
        this.consumes = consumes;
    }

    public BaselineStatus getClassification() {
        return classification;
    }

    public void setClassification(BaselineStatus classification) {
        this.classification = classification;
    }

    public Long getIdMaster() {
        return idMaster;
    }

    public void setIdMaster(Long idMaster) {
        this.idMaster = idMaster;
    }

    public boolean isDateChanged(BaselineWorkpackDto compare) {
        if ("Milestone".equals(this.getType())) {
            if (this.date == null && compare.getDate() != null) {
                return true;
            }
            if (this.date != null && !this.date.equals(compare.getDate())) {
                return true;
            }
        }
        return false;
    }

    public boolean isScheduleChanged(BaselineWorkpackDto compare) {
        if (CollectionUtils.isEmpty(compare.getSchedule()) && CollectionUtils.isNotEmpty(this.getSchedule())) {
            return true;
        }
        BaselineScheduleStep schedulePrincipal = this.schedule.stream().filter(s -> s.getIdMaster().equals(this.getIdMaster())).findFirst().orElse(null);
        BaselineScheduleStep scheduleCompare = compare.getSchedule().stream().filter(s -> s.getIdMaster().equals(this.getIdMaster())).findFirst().orElse(null);
        if (schedulePrincipal != null) {
            if (scheduleCompare == null){
                return true;
            }
            if (schedulePrincipal.getEnd() != null && !schedulePrincipal.getEnd().equals(scheduleCompare.getEnd())) {
                return true;
            }
            if (schedulePrincipal.getStart() != null && !schedulePrincipal.getStart().equals(scheduleCompare.getStart())) {
                return true;
            }
        }
        return false;
    }

    public boolean isStepChanged(BaselineWorkpackDto compare) {
        BigDecimal plannedWorkPrincipal = this.schedule.stream().filter(
            s -> s.getIdMaster().equals(this.getIdMaster())).map(BaselineScheduleStep::getPlannedWork).filter(
            Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal plannedWorkCompare = compare.getSchedule().stream().filter(
            s -> s.getIdMaster().equals(this.getIdMaster())).map(BaselineScheduleStep::getPlannedWork).filter(
            Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        return plannedWorkPrincipal.compareTo(plannedWorkCompare) != 0;
    }

    public boolean isConsumesChanged(BaselineWorkpackDto compare) {
        if (CollectionUtils.isEmpty(this.consumes) && CollectionUtils.isEmpty(compare.getConsumes())) {
            return false;
        }
        if ((CollectionUtils.isNotEmpty(this.consumes) && CollectionUtils.isEmpty(compare.getConsumes()))
            || (CollectionUtils.isEmpty(this.consumes) && CollectionUtils.isNotEmpty(compare.getConsumes()))) {
            return true;
        }

        BigDecimal consumePrincipal = this.consumes.stream().filter(c -> c.getIdMaster().equals(this.getIdMaster())).map(
            BaselineConsumesStep::getPlannedCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal consumeCompare = compare.getConsumes().stream().filter(c -> c.getIdMaster().equals(this.getIdMaster())).map(
            BaselineConsumesStep::getPlannedCost).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);


        return consumePrincipal.compareTo(consumeCompare) != 0;
    }

}
