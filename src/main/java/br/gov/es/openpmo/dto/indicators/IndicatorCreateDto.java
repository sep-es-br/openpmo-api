package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.dto.indicators.period.PeriodGoalDto;
import br.gov.es.openpmo.model.indicators.PeriodGoal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class IndicatorCreateDto {

    @NotNull
    private Long idWorkpack;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private String source;

    @NotNull
    @NotEmpty
    private String measure;

    @NotNull
    @NotEmpty
    private String finalGoal;

    @NotNull
    @NotEmpty
    private String periodicity;

    @NotNull
    private List<PeriodGoalDto> expectedGoals;

    @NotNull
    private List<PeriodGoalDto> achievedGoals;

    @NotNull
    @NotEmpty
    private String lastUpdate;

    public IndicatorCreateDto() {
    }

    public IndicatorCreateDto(
        final Long idWorkpack,
        final String name,
        final String description,
        final String source,
        final String measure,
        final String finalGoal,
        final String periodicity,
        final List<PeriodGoalDto> expectedGoals,
        final List<PeriodGoalDto> achievedGoals,
        final String lastUpdate
    ) {
        this.idWorkpack = idWorkpack;
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.finalGoal = finalGoal;
        this.periodicity = periodicity;
        this.expectedGoals = expectedGoals;
        this.achievedGoals = achievedGoals;
        this.lastUpdate = lastUpdate;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getFinalGoal() {
        return finalGoal;
    }

    public void setFinalGoal(String finalGoal) {
        this.finalGoal = finalGoal;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public List<PeriodGoalDto> getExpectedGoals() {
        return expectedGoals;
    }

    public void setExpectedGoals(List<PeriodGoalDto> expectedGoals) {
        this.expectedGoals = expectedGoals;
    }

    public List<PeriodGoalDto> getAchievedGoals() {
        return achievedGoals;
    }

    public void setAchievedGoals(List<PeriodGoalDto> achievedGoals) {
        this.achievedGoals = achievedGoals;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
